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

        //Kafka 이벤트 (비동기 fan-out)
        feedEventProducer.publishFeedEvent(new FeedCreatedEvent(
                feed.getId(),
                userId,
                feed.getCreatedAt()
        ));
        pushToTimeline(feed);
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

        List<Feed> feeds = feedRepository.findByIdsWithRelations(feedIds);

        Map<Long, Feed> feedMap = feeds
                .stream()
                .filter(feed -> permissionValidator.canView(userId, feed))
                .collect(Collectors.toMap(Feed::getId, f -> f));

        Map<Long, UserInfoResponseDto> userMap =
                userQueryService.loadUsers(feeds
                        .stream()
                        .map(Feed::getUserId)
                        .collect(Collectors.toSet())
                );

        Map<Long, List<FeedMediaResponseDto>> mediaMap =
                feedMediaRepository.findByFeedIdIn(feedIds)
                        .stream()
                        .collect(Collectors.groupingBy(
                                fm -> fm.getFeed().getId(),
                                Collectors.mapping(
                                        FeedMediaResponseDto::from,
                                        Collectors.toList())
                        ));

        Set<Long> likedFeedIds = new HashSet<>
                (feedLikeRepository.findFeedIdsByUserIdAndFeedIdIn(userId, feedIds));

        Set<Long> bookmarkedFeedIds = new HashSet<>
                (feedBookmarkRepository.findFeedIdsByUserIdAndFeedIdIn(userId, feedIds));

        List<FeedResponseDto> content =
                timelines.getContent()
                        .stream()
                        .map(tl -> {
                            Feed feed = feedMap.get(tl.getFeedId());
                            return FeedResponseDto.of(
                                    feed,
                                    userMap.get(feed.getUserId()),
                                    mediaMap.getOrDefault(feed.getId(), List.of()),
                                    likedFeedIds.contains(feed.getId()),
                                    bookmarkedFeedIds.contains(feed.getId())
                            );
                        })
                        .toList();

        return new PageImpl<>(content, pageable, timelines.getTotalElements());
    }

    public TimelineResponseDto getTimeline(Long userId, LocalDateTime cursorCreatedAt, Long cursorId, int size) {

        List<FeedTimeline> timelines = feedTimelineRepository.findTimeline(userId, cursorCreatedAt, cursorId, size + 1);

        boolean hasNext = timelines.size() > size;

        if (hasNext) timelines.remove(size);

        List<Long> feedIds = timelines.stream().map(FeedTimeline::getFeedId).toList();

        List<Feed> feeds = feedRepository.findByIdsWithRelations(feedIds);

        Map<Long, Feed> feedMap =
                feeds.stream().collect(Collectors.toMap(Feed::getId, f -> f));

        Map<Long, UserInfoResponseDto> userMap =
                userQueryService.loadUsers(
                        feeds.stream().map(Feed::getUserId).collect(Collectors.toSet())
                );

        Map<Long, List<FeedMediaResponseDto>> mediaMap = feedMediaRepository.findByFeedIdIn(feedIds)
                        .stream()
                        .collect(Collectors.groupingBy(
                                fm -> fm.getFeed().getId(),
                                Collectors.mapping(FeedMediaResponseDto::from, Collectors.toList())
                        ));

        List<FeedResponseDto> feedDtos = timelines
                .stream()
                .map(tl -> {
                    Feed feed = feedMap.get(tl.getFeedId());
                    return FeedResponseDto.of(
                            feed,
                            userMap.get(feed.getUserId()),
                            mediaMap.getOrDefault(feed.getId(), List.of()),
                            false,
                            false
                    );
                })
                .toList();

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

        return FeedResponseDto.of(feed, userInfo, medias, false, false);
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
        feedTimelineRepository.save(
                new FeedTimeline(feed.getUserId(), feed.getId())
        );

        // 2. 팔로워 타임라인
        List<Long> followerIds =
                followQueryService.getFollowers(feed.getUserId());

        List<FeedTimeline> timelines = followerIds.stream()
                .map(followerId -> new FeedTimeline(followerId, feed.getId()))
                .toList();

        feedTimelineRepository.saveAll(timelines);
    }
}
