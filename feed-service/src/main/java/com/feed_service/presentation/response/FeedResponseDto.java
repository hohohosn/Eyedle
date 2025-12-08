package com.feed_service.presentation.response;
import com.feed_service.domain.model.Feed;
import com.feed_service.domain.model.FeedPermission;
import com.feed_service.presentation.request.FeedMediaRequestDto;
import lombok.Getter;

import java.util.List;

@Getter
public class FeedResponseDto {

    private final Long id;
    private final Long userId;
    private final String content;
    private final FeedPermission permission;
    private final List<FeedMediaRequestDto> medias;

    public FeedResponseDto(Feed feed) {
        this.id = feed.getId();
        this.userId = feed.getUserId();
        this.content = feed.getContent();
        this.permission = feed.getPermission();
        this.medias = feed.getMediaList().stream()
                .map(FeedMediaRequestDto::new)
                .toList();
    }

    public static FeedResponseDto mapFeed(Feed feed) {
        return new FeedResponseDto(feed);
    }
}
