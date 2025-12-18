package com.feed_service.application.service;

import com.common.exception.CustomException;
import com.common.response.ErrorCode;
import com.feed_service.domain.model.Feed;
import com.feed_service.domain.repository.FeedBookmarkRepository;
import com.feed_service.domain.repository.FeedLikeRepository;
import com.feed_service.domain.repository.FeedRepository;
import com.feed_service.infra.user.dto.UserInfoResponseDto;
import com.feed_service.infra.user.service.UserQueryService;
import com.feed_service.presentation.request.FeedCreateRequestDto;
import com.feed_service.presentation.request.FeedUpdateRequestDto;
import com.feed_service.presentation.response.FeedResponseDto;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;

import java.util.Set;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class FeedService {

    private final FeedRepository feedRepository;
    private final TagService tagService;
    private final UserQueryService userQueryService;
    private final FeedLikeRepository feedLikeRepository;
    private final FeedBookmarkRepository feedBookmarkRepository;

    @Transactional
    public Long createFeed(FeedCreateRequestDto request, Long userId) {

        Feed feed = Feed.builder()
                .userId(userId)
                .content(request.getContent())
                .permission(request.getPermission())
                .build();

        feedRepository.save(feed);
        tagService.applyTags(feed, request.getTags());

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

        Page<Feed> feeds = feedRepository.findFeeds(pageable);

        List<Long> feedIds = feeds.getContent().stream()
                .map(Feed::getId)
                .toList();

        Set<Long> likedFeedIds = new HashSet<>(
                feedLikeRepository.findFeedIdsByUserIdAndFeedIdIn(userId, feedIds)
        );

        Set<Long> bookmarkedFeedIds = new HashSet<>(
                feedBookmarkRepository.findFeedIdsByUserIdAndFeedIdIn(userId, feedIds)
        );

        return feeds.map(feed -> {
            UserInfoResponseDto userInfo =
                    userQueryService.loadUser(feed.getUserId());

            return FeedResponseDto.of(
                    feed,
                    userInfo,
                    likedFeedIds.contains(feed.getId()),
                    bookmarkedFeedIds.contains(feed.getId())
            );
        });
    }

    @Transactional
    public FeedResponseDto updateFeed(
            Long feedId,
            FeedUpdateRequestDto request,
            Long userId
    ) {

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
}
