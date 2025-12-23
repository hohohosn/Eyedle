package com.feed_service.presentation.response;
import com.feed_service.domain.model.Feed;
import com.feed_service.domain.model.FeedPermission;
import com.feed_service.infra.user.dto.FeedUserDto;
import com.feed_service.infra.user.dto.UserInfoResponseDto;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
public class FeedResponseDto {

    private final Long id;
    private final FeedUserDto user;
    private final String content;
    private final FeedPermission permission;
    private final List<FeedMediaResponseDto> medias;
    private final List<String> tags;

    private final boolean liked;
    private final boolean bookmarked;

    @Builder
    public FeedResponseDto(
            Long id,
            FeedUserDto user,
            String content,
            FeedPermission permission,
            List<FeedMediaResponseDto> medias,
            List<String> tags,
            boolean liked,
            boolean bookmarked
    ) {
        this.id = id;
        this.user = user;
        this.content = content;
        this.permission = permission;
        this.medias = medias;
        this.tags = tags;
        this.liked = liked;
        this.bookmarked = bookmarked;
    }

    public static FeedResponseDto of(
            Feed feed,
            UserInfoResponseDto userInfo,
            List<FeedMediaResponseDto> medias,
            boolean liked,
            boolean bookmarked
    ) {
        FeedUserDto userDto = new FeedUserDto(
                userInfo.getId(),
                userInfo.getUsername(),
                userInfo.getEmail()
        );

        return FeedResponseDto.builder()
                .id(feed.getId())
                .user(userDto)
                .content(feed.getContent())
                .permission(feed.getPermission())
                .medias(medias)
                .tags(feed.getFeedTags().stream()
                        .map(ft -> ft.getTag().getName())
                        .toList())
                .liked(liked)
                .bookmarked(bookmarked)
                .build();
    }
}