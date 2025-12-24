package com.feed_service.domain.repository;

import com.feed_service.domain.model.Feed;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface FeedRepository extends JpaRepository<Feed, Long>, FeedRepositoryCustom{
    List<Feed> findByTagNameAndSince(String tagName, LocalDateTime since, int limit);
}
