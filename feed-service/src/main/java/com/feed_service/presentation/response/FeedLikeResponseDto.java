package com.feed_service.presentation.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class FeedLikeResponseDto {
    private boolean liked;
    private long likeCount;
}

