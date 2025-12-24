package com.feed_service.application.service;

import com.common.exception.CustomException;
import com.common.response.ErrorCode;
import com.feed_service.domain.model.Feed;
import com.feed_service.domain.model.FeedTimeline;
import com.feed_service.domain.repository.*;
import com.feed_service.infra.kafka.producer.FeedEventProducer;
import com.feed_service.infra.user.dto.UserInfoResponseDto;
import com.feed_service.infra.user.service.UserQueryService;
import com.feed_service.presentation.request.FeedCreateRequestDto;
import com.feed_service.infra.kafka.event.FeedCreatedEvent;
import com.feed_service.presentation.response.FeedMediaResponseDto;
import com.feed_service.presentation.response.FeedResponseDto;
import com.feed_service.presentation.response.TimelineResponseDto;
import org.springframework.data.domain.PageImpl;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class FeedService {

    private final FeedRepository feedRepository;
    private final TagService tagService;
    private final UserQueryService userQueryService;
    private final FeedLikeRepository feedLikeRepository;
    private final FeedBookmarkRepository feedBookmarkRepository;
    private final FollowQueryService followQueryService;
    private final FeedTimelineRepository feedTimelineRepository;
    private final FeedEventProducer feedEventProducer;
    private final FeedMediaService feedMediaService;
    private final FeedMediaRepository feedMediaRepository;
    private final FeedPermissionValidator permissionValidator;

    @Transactional
    public Long createFeed(FeedCreateRequestDto request, Long userId, List<MultipartFile> medias) {
        Feed feed = Feed.builder()
                .userId(userId)
                .content(request.getContent())
                .permission(request.getPermission())
                .build();

        feedRepository.save(feed);
        tagService.applyTags(feed, request.getTags());
        feedMediaService.uploadMedias(feed, medias);

        feedEventProducer.publishFeedEvent(new FeedCreatedEvent(feed.getId(), userId, feed.getCreatedAt()));

        // 내 타임라인만 일단 저장
        feedTimelineRepository.save(new FeedTimeline(userId, feed.getId()));

        return feed.getId();
    }

    public FeedResponseDto findFeed(Long feedId, Long userId) {
        Feed feed = feedRepository.findById(feedId)
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND));

        permissionValidator.validateView(userId, feed);

        UserInfoResponseDto userInfo = userQueryService.loadUser(feed.getUserId());

        boolean liked = feedLikeRepository.existsByFeed_IdAndUserId(feedId, userId);
        boolean bookmarked = feedBookmarkRepository.existsByFeed_IdAndUserId(feedId, userId);

        List<FeedMediaResponseDto> medias = feedMediaRepository.findByFeedIdOrderByOrderIndexAsc(feedId)
                .stream()
                .map(FeedMediaResponseDto::from)
                .toList();

        return FeedResponseDto.of(feed, userInfo, medias, liked, bookmarked);
    }

    public Page<FeedResponseDto> findAllFeeds(Pageable pageable, Long userId) {
        Page<FeedTimeline> timelines = feedTimelineRepository.findByUserId(userId, pageable);

        List<Long> feedIds = timelines.getContent()
                .stream()
                .map(FeedTimeline::getFeedId)
                .toList();

        if (feedIds.isEmpty()) {
            return Page.empty(pageable);
        }

        //권한 체크 가능한 피드만 필터링
        List<Feed> feeds = feedRepository.findByIdsWithRelations(feedIds)
                .stream()
                .filter(feed -> permissionValidator.canView(userId, feed))
                .toList();

        if (feeds.isEmpty()) {
            return Page.empty(pageable);
        }

        //필터링된 피드 기준으로 Map 생성
        Map<Long, Feed> feedMap = feeds.stream()
                .collect(Collectors.toMap(Feed::getId, f -> f));

        Set<Long> validFeedIds = feedMap.keySet();

        Map<Long, UserInfoResponseDto> userMap = userQueryService.loadUsers(
                feeds.stream()
                        .map(Feed::getUserId)
                        .collect(Collectors.toSet())
        );

        //유효한 피드 ID만 조회
        Map<Long, List<FeedMediaResponseDto>> mediaMap = feedMediaRepository.findByFeedIdIn(new ArrayList<>(validFeedIds))
                .stream()
                .collect(Collectors.groupingBy(
                        fm -> fm.getFeed().getId(),
                        Collectors.mapping(FeedMediaResponseDto::from, Collectors.toList())
                ));

        Set<Long> likedFeedIds = new HashSet<>(
                feedLikeRepository.findFeedIdsByUserIdAndFeedIdIn(userId, new ArrayList<>(validFeedIds))
        );

        Set<Long> bookmarkedFeedIds = new HashSet<>(
                feedBookmarkRepository.findFeedIdsByUserIdAndFeedIdIn(userId, new ArrayList<>(validFeedIds))
        );

        List<FeedResponseDto> content = timelines.getContent()
                .stream()
                .map(tl -> {
                    Feed feed = feedMap.get(tl.getFeedId());
                    if (feed == null) {
                        return null;
                    }
                    UserInfoResponseDto userInfo = userMap.get(feed.getUserId());
                    if (userInfo == null) {
                        return null;
                    }
                    return FeedResponseDto.of(
                            feed,
                            userInfo,
                            mediaMap.getOrDefault(feed.getId(), List.of()),
                            likedFeedIds.contains(feed.getId()),
                            bookmarkedFeedIds.contains(feed.getId())
                    );
                })
                .filter(Objects::nonNull)
                .toList();

        return new PageImpl<>(content, pageable, timelines.getTotalElements());
    }

    public TimelineResponseDto getTimeline(Long userId, LocalDateTime cursorCreatedAt, Long cursorId, int size) {
        // 타임라인 조회
        List<FeedTimeline> timelines = feedTimelineRepository.findTimeline(userId, cursorCreatedAt, cursorId, size + 1);

        if (timelines.isEmpty()) {
            return new TimelineResponseDto(List.of(), false, null, null);
        }

        boolean hasNext = timelines.size() > size;
        if (hasNext) timelines.remove(size);

        List<Long> feedIds = timelines.stream()
                .map(FeedTimeline::getFeedId)
                .toList();

        //권한 체크 추가
        List<Feed> feeds = feedRepository.findByIdsWithRelations(feedIds)
                .stream()
                .filter(feed -> permissionValidator.canView(userId, feed))
                .toList();

        if (feeds.isEmpty()) {
            FeedTimeline last = timelines.get(timelines.size() - 1);
            return new TimelineResponseDto(List.of(), hasNext, last.getCreatedAt(), last.getId());
        }

        Map<Long, Feed> feedMap = feeds.stream()
                .collect(Collectors.toMap(Feed::getId, f -> f));

        Set<Long> validFeedIds = feedMap.keySet();

        Map<Long, UserInfoResponseDto> userMap = userQueryService.loadUsers(
                feeds.stream()
                        .map(Feed::getUserId)
                        .collect(Collectors.toSet())
        );

        //유효한 피드 ID만 조회
        Map<Long, List<FeedMediaResponseDto>> mediaMap = feedMediaRepository.findByFeedIdIn(new ArrayList<>(validFeedIds))
                .stream()
                .collect(Collectors.groupingBy(
                        fm -> fm.getFeed().getId(),
                        Collectors.mapping(FeedMediaResponseDto::from, Collectors.toList())
                ));

        //좋아요/북마크 정보 실제 조회
        Set<Long> likedFeedIds = new HashSet<>(
                feedLikeRepository.findFeedIdsByUserIdAndFeedIdIn(userId, new ArrayList<>(validFeedIds))
        );

        Set<Long> bookmarkedFeedIds = new HashSet<>(
                feedBookmarkRepository.findFeedIdsByUserIdAndFeedIdIn(userId, new ArrayList<>(validFeedIds))
        );

        List<FeedResponseDto> feedDtos = timelines.stream()
                .map(tl -> {
                    Feed feed = feedMap.get(tl.getFeedId());
                    if (feed == null) return null;

                    UserInfoResponseDto userInfo = userMap.get(feed.getUserId());
                    if (userInfo == null) return null;

                    return FeedResponseDto.of(
                            feed,
                            userInfo,
                            mediaMap.getOrDefault(feed.getId(), List.of()),
                            likedFeedIds.contains(feed.getId()),
                            bookmarkedFeedIds.contains(feed.getId())
                    );
                })
                .filter(Objects::nonNull)
                .toList();

        if (feedDtos.isEmpty()) {
            FeedTimeline last = timelines.get(timelines.size() - 1);
            return new TimelineResponseDto(List.of(), hasNext, last.getCreatedAt(), last.getId());
        }

        FeedTimeline last = timelines.get(timelines.size() - 1);

        return new TimelineResponseDto(
                feedDtos,
                hasNext,
                last.getCreatedAt(),
                last.getId()
        );
    }

    @Transactional
    public FeedResponseDto updateFeed(Long feedId, FeedCreateRequestDto request, Long userId, List<MultipartFile> images) {
        Feed feed = feedRepository.findById(feedId)
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND));

        permissionValidator.validateModify(userId, feed);

        feed.updateFeed(request.getContent(), request.getPermission());
        tagService.updateTags(feed, request.getTags());
        feedMediaService.replaceImages(feed, images);

        UserInfoResponseDto userInfo = userQueryService.loadUser(feed.getUserId());

        List<FeedMediaResponseDto> medias = feedMediaRepository.findByFeedIdOrderByOrderIndexAsc(feedId)
                .stream()
                .map(FeedMediaResponseDto::from)
                .toList();

        boolean liked = feedLikeRepository.existsByFeed_IdAndUserId(feedId, userId);
        boolean bookmarked = feedBookmarkRepository.existsByFeed_IdAndUserId(feedId, userId);

        return FeedResponseDto.of(feed, userInfo, medias, liked, bookmarked);
    }

    @Transactional
    public void deleteFeed(Long feedId, Long userId) {
        Feed feed = feedRepository.findById(feedId)
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND));

        permissionValidator.validateModify(userId, feed);

        if (feed.isDeleted()) {
            throw new CustomException(ErrorCode.NOT_FOUND);
        }

        feed.statusDeleted();
    }

    private void pushToTimeline(Feed feed) {
        // 1. 내 타임라인
        feedTimelineRepository.save(new FeedTimeline(feed.getUserId(), feed.getId()));

        // 2. 팔로워 타임라인
        List<Long> followerIds = followQueryService.getFollowers(feed.getUserId());

        List<FeedTimeline> timelines = followerIds.stream()
                .map(followerId -> new FeedTimeline(followerId, feed.getId()))
                .toList();

        feedTimelineRepository.saveAll(timelines);
    }
}