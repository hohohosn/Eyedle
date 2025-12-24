package com.feed_service.domain.repository;

import com.feed_service.domain.model.FeedTimeline;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FeedTimelineRepository extends JpaRepository<FeedTimeline, Long>, FeedTimelineRepositoryCustom{

    Page<FeedTimeline> findByUserId(Long userId, Pageable pageable);

}
