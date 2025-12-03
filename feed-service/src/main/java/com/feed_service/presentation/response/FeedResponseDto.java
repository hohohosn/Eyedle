package com.feed_service.presentation.response;

import com.feed_service.domain.model.Feed;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class FeedResponseDto {

    private Long feedId;
    private Long userId;
    private String content;
    private String permission;

    public static FeedResponseDto mapFeed(Feed feed) {
        return FeedResponseDto.builder()
                .feedId(feed.getId())
                .userId(feed.getUserId())
                .content(feed.getContent())
                .permission(feed.getPermission().name())
                .build();
    }
}
