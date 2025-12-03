package com.feed_service.domain.repository;

public interface FeedBookmarkRepository {
    boolean existsByFeedIdAndUserId(Long feedId, Long userId);
}
