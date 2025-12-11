package com.feed_service.domain.repository;

import com.feed_service.domain.model.FeedBookmark;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FeedBookmarkRepository extends JpaRepository<FeedBookmark, Long> {

    boolean existsByFeed_IdAndUserId(Long feedId, Long userId);

    void deleteByFeed_IdAndUserId(Long feedId, Long userId);

    long countByFeed_Id(Long feedId);

    List<FeedBookmark> findByUserIdOrderByCreatedAtDesc(Long userId);
}

