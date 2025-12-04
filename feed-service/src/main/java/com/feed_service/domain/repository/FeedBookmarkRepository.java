package com.feed_service.domain.repository;

import com.feed_service.domain.model.FeedBookmark;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FeedBookmarkRepository extends JpaRepository<FeedBookmark, Long> {

    boolean existsByFeed_IdAndUserId(Long feedId, Long userId);

    void deleteByFeed_IdAndUserId(Long feedId, Long userId);
}
