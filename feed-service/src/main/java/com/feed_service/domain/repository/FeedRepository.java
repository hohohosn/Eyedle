package com.feed_service.domain.repository;

import com.feed_service.domain.model.Feed;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface FeedRepository extends JpaRepository<Feed, Long>, FeedRepositoryCustom{
    @Query("""
        select distinct f
        from Feed f
        left join fetch f.feedTags
        where f.id in :ids
    """)
    List<Feed> findByIdsWithRelations(@Param("ids") List<Long> ids);
}
