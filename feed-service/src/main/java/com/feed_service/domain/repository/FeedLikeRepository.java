package com.feed_service.domain.repository;

import com.feed_service.domain.model.FeedLike;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FeedLikeRepository extends JpaRepository<FeedLike, Long> {

    boolean existsByFeed_IdAndUserId(Long feedId, Long userId);

    void deleteByFeed_IdAndUserId(Long feedId, Long userId);

    @Query("""
        SELECT fl.feed.id
        FROM FeedLike fl
        WHERE fl.userId = :userId
          AND fl.feed.id IN :feedIds
    """)
    List<Long> findFeedIdsByUserIdAndFeedIdIn(@Param("userId") Long userId, @Param("feedIds") List<Long> feedIds);

    List<FeedLike> findByUserIdOrderByCreatedAtDesc(Long userId);

    long countByFeed_Id(Long feedId);

    //벌크 좋아요 수 조회
    @Query("""
        SELECT fl.feed.id AS feedId, COUNT(fl) AS likeCount
        FROM FeedLike fl
        WHERE fl.feed.id IN :feedIds
        GROUP BY fl.feed.id
    """)
    List<FeedLikeCountProjection> countByFeedIds(@Param("feedIds") List<Long> feedIds);

    interface FeedLikeCountProjection {
        Long getFeedId();
        Long getLikeCount();
    }
}

