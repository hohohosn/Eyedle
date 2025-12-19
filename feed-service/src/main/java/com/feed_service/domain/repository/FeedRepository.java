package com.feed_service.domain.repository;

import com.feed_service.domain.model.Feed;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FeedRepository extends JpaRepository<Feed, Long>, FeedRepositoryCustom{
    List<Feed> findByIdsWithRelations(List<Long> attr0);
}
