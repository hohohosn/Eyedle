package com.feed_service.domain.repository;

import com.feed_service.domain.model.Feed;
import com.feed_service.domain.model.FeedTag;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FeedTagRepository extends JpaRepository<FeedTag, Long> {
    void deleteAllByFeed(Feed feed);
}
