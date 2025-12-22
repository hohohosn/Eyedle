package com.feed_service.domain.repository;

import com.feed_service.domain.model.FeedMedia;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FeedMediaRepository extends JpaRepository<FeedMedia, Long> {
    List<FeedMedia> findByFeedId(Long feedId);
    List<FeedMedia> findByFeedIdOrderByOrderIndexAsc(Long feedId);
    List<FeedMedia> findByFeedIdIn(List<Long> feedIds);
}
