package com.feed_service.application.service;

import com.feed_service.domain.model.Feed;
import com.feed_service.domain.repository.FeedLikeRepository;
import com.feed_service.domain.repository.FeedRepository;
import com.feed_service.infra.user.dto.UserInfoResponseDto;
import com.feed_service.infra.user.service.UserQueryService;
import com.feed_service.presentation.response.FeedRecentResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class FeedRecentService {

    private final FeedRepository feedRepository;
    private final FeedLikeRepository feedLikeRepository;
    private final UserQueryService userQueryService;

    public List<FeedRecentResponseDto> findRecentFeeds(
            LocalDateTime since,
            String keyword
    ) {

        List<Feed> feeds = feedRepository.findRecentFeeds(since, keyword);

        return feeds.stream()
                .map(feed -> {
                    UserInfoResponseDto user = userQueryService.loadUser(feed.getUserId());

                    return FeedRecentResponseDto.builder()
                            .feedId(feed.getId())
                            .content(feed.getContent())
                            .tags(feed.getFeedTags().stream()
                                    .map(ft -> ft.getTag().getName())
                                    .toList())
                            .userId(user.getId())
                            .username(user.getUsername())
                            .profileUrl(null) // 아직 없으면 null
                            .mainImageUrl(
                                    feed.getMediaList().isEmpty()
                                            ? null
                                            : feed.getMediaList().get(0).getMediaUrl()
                            )
                            .likeCount(
                                    feedLikeRepository.countByFeed_Id(feed.getId())
                            )
                            .createdAt(feed.getCreatedAt())
                            .isDeleted(feed.isDeleted())
                            .build();
                })
                .toList();
    }
}