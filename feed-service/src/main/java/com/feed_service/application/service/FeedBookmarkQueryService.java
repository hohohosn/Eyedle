package com.feed_service.application.service;

import com.feed_service.domain.model.FeedBookmark;
import com.feed_service.domain.repository.FeedBookmarkRepository;
import com.feed_service.presentation.response.FeedSummaryDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestHeader;

import java.util.List;

@Service
@RequiredArgsConstructor
public class FeedBookmarkQueryService {

    private final FeedBookmarkRepository feedBookmarkRepository;

    public List<FeedSummaryDto> getUserBookmarkedFeeds(@RequestHeader("X-User-Id") Long userId) {

        List<FeedBookmark> bookmarks = feedBookmarkRepository.findByUserIdOrderByCreatedAtDesc(userId);

        return bookmarks.stream()
                .map(bm -> FeedSummaryDto.from(
                        bm.getFeed(),
                        false,
                        true
                ))
                .toList();
    }
}
