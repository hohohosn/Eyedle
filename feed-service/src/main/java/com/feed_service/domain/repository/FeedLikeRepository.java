package com.feed_service.domain.repository;

import com.feed_service.domain.model.FeedLike;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface FeedLikeRepository extends JpaRepository<FeedLike, Long> {


    boolean existsByFeed_IdAndUserId(Long feedId, Long userId);
    void deleteByFeed_IdAndUserId(Long feedId, Long userId);

    @Query("""
        select fl.feed.id
        from FeedLike fl
        where fl.userId = :userId
          and fl.feed.id in :feedIds
    """)

    List<Long> findFeedIdsByUserIdAndFeedIdIn(Long userId, List<Long> feedIds);

    List<FeedLike> findByUserIdOrderByCreatedAtDesc(Long userId);

}

