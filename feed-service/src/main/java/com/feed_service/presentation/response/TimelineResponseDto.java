package com.feed_service.presentation.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@AllArgsConstructor
public class TimelineResponseDto {

    private List<FeedResponseDto> feeds;
    private boolean hasNext;

    private LocalDateTime nextCursorCreatedAt;
    private Long nextCursorId;
}