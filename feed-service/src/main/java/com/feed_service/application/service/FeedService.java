package com.feed_service.application.service;

import com.common.exception.CustomException;
import com.common.response.ErrorCode;
import com.feed_service.domain.model.Feed;
import com.feed_service.domain.model.FeedTimeline;
import com.feed_service.domain.repository.FeedBookmarkRepository;
import com.feed_service.domain.repository.FeedLikeRepository;
import com.feed_service.domain.repository.FeedRepository;
import com.feed_service.domain.repository.FeedTimelineRepository;
import com.feed_service.infra.kafka.producer.FeedEventProducer;
import com.feed_service.infra.user.dto.UserInfoResponseDto;
import com.feed_service.infra.user.service.UserQueryService;
import com.feed_service.presentation.request.FeedCreateRequestDto;
import com.feed_service.presentation.request.FeedUpdateRequestDto;
import com.feed_service.infra.kafka.event.FeedCreatedEvent;
import com.feed_service.presentation.response.FeedResponseDto;
import com.feed_service.presentation.response.TimelineResponseDto;
import org.springframework.data.domain.PageImpl;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;

import java.util.Map;
import java.util.Set;
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

    @Transactional
    public Long createFeed(FeedCreateRequestDto request, Long userId) {

        Feed feed = Feed.builder()
                .userId(userId)
                .content(request.getContent())
                .permission(request.getPermission())
                .build();

        feedRepository.save(feed);
        tagService.applyTags(feed, request.getTags());

        feedEventProducer.publishFeedEvent(new FeedCreatedEvent(feed
                .getId(),
                userId,
                feed.getCreatedAt()
        ));



        return feed.getId();
    }

    public FeedResponseDto findFeed(Long feedId, Long userId) {

        Feed feed = feedRepository.findById(feedId)
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND));

        UserInfoResponseDto userInfo =
                userQueryService.loadUser(feed.getUserId());

        boolean liked = feedLikeRepository.existsByFeed_IdAndUserId(feedId, userId);
        boolean bookmarked = feedBookmarkRepository.existsByFeed_IdAndUserId(feedId, userId);

        return FeedResponseDto.of(feed, userInfo, liked, bookmarked);
    }

    public Page<FeedResponseDto> findAllFeeds(Pageable pageable, Long userId) {

        Page<FeedTimeline> timelines = feedTimelineRepository.
                findByUserId(userId, pageable);

        List<Long> feedIds = timelines.getContent()
                .stream()
                .map(FeedTimeline::getFeedId)
                .toList();

        if(feedIds.isEmpty()) {
            return Page.empty(pageable);
        }

        List<Feed> feeds = feedRepository.findByIdsWithRelations((feedIds));

        Map<Long, Feed> feedMap = feeds
                .stream()
                .collect(Collectors.toMap(Feed::getId, feed -> feed));

        Set<Long> authorIds = feeds
                .stream()
                .map(Feed::getUserId)
                .collect(Collectors.toSet());

        Map<Long, UserInfoResponseDto> userMap =
                userQueryService.loadUsers(authorIds);

        Set<Long> likedFeedIds = new HashSet<>(
                feedLikeRepository.findFeedIdsByUserIdAndFeedIdIn(userId, feedIds)
        );

        Set<Long> bookmarkedFeedIds = new HashSet<>(
                feedBookmarkRepository.findFeedIdsByUserIdAndFeedIdIn(userId, feedIds)
        );

        List<FeedResponseDto> content = timelines.getContent().stream()
                .map(tl -> {
                    Feed feed = feedMap.get(tl.getFeedId());
                    UserInfoResponseDto userInfo =
                            userMap.get(feed.getUserId());

                    return FeedResponseDto.of(
                            feed,
                            userInfo,
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

        if(hasNext) timelines.remove(size);

        List<Long> feedIds = timelines
                .stream()
                .map(FeedTimeline::getFeedId)
                .toList();

        List<Feed> feeds = feedRepository.findByIdsWithRelations((feedIds));

        Map<Long, Feed> feedMap = feeds
                .stream()
                .collect(Collectors.toMap(Feed::getId, f -> f));

        Set<Long> authorIds = feeds
                .stream()
                .map(Feed::getUserId)
                .collect(Collectors.toSet());

        Map<Long, UserInfoResponseDto> userMap =
                userQueryService.loadUsers(authorIds);

        List<FeedResponseDto> feedDtos = timelines.stream()
                .map(tl -> {
                    Feed feed = feedMap.get(tl.getFeedId());
                    return FeedResponseDto.of(
                            feed,
                            userMap.get(feed.getUserId()),
                            false,
                            false
                    );
                })
                .toList();

        FeedTimeline last = timelines.get(timelines.size() - 1);

        return new TimelineResponseDto(feedDtos, hasNext, last.getCreatedAt(), last.getId());
    }

    @Transactional
    public FeedResponseDto updateFeed(Long feedId, FeedUpdateRequestDto request, Long userId) {

        Feed feed = feedRepository.findById(feedId)
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND));

        validateOwner(feed, userId);

        feed.updateFeed(request.getContent(), request.getPermission());
        tagService.updateTags(feed, request.getTags());

        UserInfoResponseDto userInfo =
                userQueryService.loadUser(feed.getUserId());

        return FeedResponseDto.of(feed, userInfo, false, false);
    }

    @Transactional
    public void deleteFeed(Long feedId, Long userId) {

        Feed feed = feedRepository.findById(feedId)
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND));

        validateOwner(feed, userId);

        if (feed.isDeleted()) {
            throw new CustomException(ErrorCode.NOT_FOUND);
        }

        feed.statusDeleted();
    }

    private void validateOwner(Feed feed, Long userId) {
        if (!feed.getUserId().equals(userId)) {
            throw new CustomException(ErrorCode.FORBIDDEN);
        }
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
