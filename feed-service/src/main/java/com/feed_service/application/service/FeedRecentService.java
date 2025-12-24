package com.feed_service.application.service;

import com.feed_service.domain.model.Feed;
import com.feed_service.domain.model.FeedMedia;
import com.feed_service.domain.repository.FeedLikeRepository;
import com.feed_service.domain.repository.FeedMediaRepository;
import com.feed_service.domain.repository.FeedRepository;
import com.feed_service.domain.repository.FeedTagRepository;
import com.feed_service.infra.user.dto.UserInfoResponseDto;
import com.feed_service.infra.user.service.UserQueryService;
import com.feed_service.presentation.response.FeedRecentResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestHeader;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class FeedRecentService {

    private final FeedRepository feedRepository;
    private final FeedLikeRepository feedLikeRepository;
    private final FeedTagRepository feedTagRepository;
    private final FeedMediaRepository feedMediaRepository;
    private final UserQueryService userQueryService;
    private final FeedPermissionValidator permissionValidator;

    public List<FeedRecentResponseDto> findRecentFeeds(
            Long userId,
            LocalDateTime since,
            String keyword
    ) {

        List<Feed> feeds = feedRepository.findRecentFeeds(since, keyword);

        return feeds.stream()
                .filter(feed -> permissionValidator.canView(userId, feed))
                .map(feed -> {

                    List<String> tags = feedTagRepository.findByFeed(feed)
                            .stream()
                            .map(ft -> ft.getTag().getName())
                            .toList();

                    String mainImageUrl = feedMediaRepository
                            .findByFeedIdOrderByOrderIndexAsc(feed.getId())
                            .stream()
                            .findFirst()
                            .map(FeedMedia::getMediaUrl)
                            .orElse(null);

                    UserInfoResponseDto user = userQueryService.loadUser(feed.getUserId());

                    return FeedRecentResponseDto.builder()
                            .feedId(feed.getId())
                            .content(feed.getContent())
                            .tags(tags)
                            .userId(user.getId())
                            .username(user.getUsername())
                            .profileUrl(null)
                            .mainImageUrl(mainImageUrl)
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