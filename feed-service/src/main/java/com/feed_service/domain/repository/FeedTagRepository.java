package com.feed_service.domain.repository;

import com.feed_service.domain.model.Feed;
import com.feed_service.domain.model.FeedTag;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FeedTagRepository extends JpaRepository<FeedTag, Long> {
    List<FeedTag> findByFeed(Feed feed);

    @Query("""
        select ft
        from FeedTag ft
        where ft.feed.id in :feedIds
    """)
    List<FeedTag> findAllByFeedIdIn(@Param("feedIds") List<Long> feedIds);

    @Modifying
    @Query("DELETE FROM FeedTag ft WHERE ft.feed = :feed")
    void deleteAllByFeed(@Param("feed") Feed feed);
}
