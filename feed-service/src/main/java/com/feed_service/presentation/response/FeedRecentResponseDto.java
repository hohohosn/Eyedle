package com.feed_service.presentation.response;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Builder
public class FeedRecentResponseDto {

    private Long feedId;
    private String content;
    private List<String> tags;

    private Long userId;
    private String username;
    private String profileUrl;

    private String mainImageUrl;
    private Long likeCount;

    private LocalDateTime createdAt;
    private boolean isDeleted;
}