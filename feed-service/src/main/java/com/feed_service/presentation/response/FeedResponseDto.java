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
    private final List<String> tags;

    public FeedResponseDto(Feed feed) {
        this.id = feed.getId();
        this.userId = feed.getUserId();
        this.content = feed.getContent();
        this.permission = feed.getPermission();

        this.medias = feed.getMediaList()
                .stream()
                .map(FeedMediaRequestDto::new)
                .toList();

        this.tags = feed.getFeedTags()
                .stream()
                .map(feedTag -> feedTag.getTag().getName())
                .toList();
    }

    public static FeedResponseDto mapFeed(Feed feed) {
        List<String> tags = feed.getFeedTags().stream()
                .map(ft -> ft.getTag().getName())
                .toList();

        return new FeedResponseDto(feed);
    }
}
