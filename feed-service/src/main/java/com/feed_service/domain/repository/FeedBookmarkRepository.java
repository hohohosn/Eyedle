package com.feed_service.domain.repository;

import com.feed_service.domain.model.FeedBookmark;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface FeedBookmarkRepository extends JpaRepository<FeedBookmark, Long> {


    boolean existsByFeed_IdAndUserId(Long feedId, Long userId);
    void deleteByFeed_IdAndUserId(Long feedId, Long userId);


    @Query("""
        select fb.feed.id
        from FeedBookmark fb
        where fb.userId = :userId
          and fb.feed.id in :feedIds
    """)

    List<Long> findFeedIdsByUserIdAndFeedIdIn(Long userId, List<Long> feedIds);

    List<FeedBookmark> findByUserIdOrderByCreatedAtDesc(Long userId);
}

