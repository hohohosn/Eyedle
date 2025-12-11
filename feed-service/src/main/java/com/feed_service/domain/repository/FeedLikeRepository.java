package com.feed_service.domain.repository;

import com.feed_service.domain.model.FeedLike;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FeedLikeRepository extends JpaRepository<FeedLike, Long> {

    boolean existsByFeed_IdAndUserId(Long feedId, Long userId);

    void deleteByFeed_IdAndUserId(Long feedId, Long userId);

    long countByFeed_Id(Long feedId);

    List<FeedLike> findByUserIdOrderByCreatedAtDesc(Long userId);
}

