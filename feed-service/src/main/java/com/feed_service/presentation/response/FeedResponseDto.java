package com.feed_service.presentation.response;
import com.feed_service.domain.model.Feed;
import com.feed_service.domain.model.FeedPermission;
import com.feed_service.presentation.request.FeedMediaRequestDto;
import lombok.Builder;
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

    private final boolean liked;
    private final boolean bookmarked;

    @Builder
    public FeedResponseDto(
            Long id,
            Long userId,
            String content,
            FeedPermission permission,
            List<FeedMediaRequestDto> medias,
            List<String> tags,
            boolean liked,
            boolean bookmarked
    ) {
        this.id = id;
        this.userId = userId;
        this.content = content;
        this.permission = permission;
        this.medias = medias;
        this.tags = tags;
        this.liked = liked;
        this.bookmarked = bookmarked;
    }

    public static FeedResponseDto of(Feed feed, boolean liked, boolean bookmarked) {

        return FeedResponseDto.builder()
                .id(feed.getId())
                .userId(feed.getUserId())
                .content(feed.getContent())
                .permission(feed.getPermission())
                .medias(feed.getMediaList().stream()
                        .map(FeedMediaRequestDto::new).toList())
                .tags(feed.getFeedTags().stream()
                        .map(ft -> ft.getTag().getName()).toList())
                .liked(liked)
                .bookmarked(bookmarked)
                .build();
    }
}