package com.feed_service.domain.repository;

import com.feed_service.domain.model.Feed;

import org.springframework.data.jpa.repository.JpaRepository;

public interface FeedRepository extends JpaRepository<Feed, Long>{
}
