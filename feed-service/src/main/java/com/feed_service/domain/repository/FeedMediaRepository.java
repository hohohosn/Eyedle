package com.feed_service.domain.repository;

import com.feed_service.domain.model.FeedMedia;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FeedMediaRepository extends JpaRepository<FeedMedia, Long> {
}
