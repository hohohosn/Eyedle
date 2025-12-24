package com.feed_service.domain.repository;

import com.feed_service.domain.model.Feed;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;

public interface FeedRepositoryCustom {

    Page<Feed> findFeeds(Pageable pageable);

    List<Feed> findRecentFeeds(LocalDateTime since, String keyword);

    List<Feed> findByIdsWithRelations(List<Long> feedIds);
}
