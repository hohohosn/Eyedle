package com.feed_service.presentation.request;

import com.feed_service.domain.model.Feed;
import com.feed_service.domain.model.FeedPermission;
import lombok.Getter;

@Getter
public class FeedCreateRequestDto {

    @jakarta.validation.constraints.NotNull
    private FeedPermission permission;

    @jakarta.validation.constraints.NotBlank
    private String content;

    public Feed toEntity(Long feedId, Long userId) {
        return Feed.builder()
                .id(feedId)
                .userId(userId)
                .content(content)
                .permission(permission)
                .createdAt(java.time.LocalDateTime.now())
                .isDeleted(false)
                .build();
    }

}
