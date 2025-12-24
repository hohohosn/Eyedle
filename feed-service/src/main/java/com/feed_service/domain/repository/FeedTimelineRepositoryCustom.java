package com.feed_service.domain.repository;

import com.feed_service.domain.model.FeedTimeline;

import java.time.LocalDateTime;
import java.util.List;

public interface FeedTimelineRepositoryCustom {
    List<FeedTimeline> findTimeline(Long userId, LocalDateTime cursorCreatedAt, Long cursorId, int size);
}
