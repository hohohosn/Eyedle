package com.feed_service.application.service;

import com.feed_service.domain.model.Feed;
import com.feed_service.domain.model.FeedBookmark;
import com.feed_service.domain.model.FeedMedia;
import com.feed_service.domain.repository.FeedBookmarkRepository;
import com.feed_service.domain.repository.FeedMediaRepository;
import com.feed_service.domain.repository.FeedTagRepository;
import com.feed_service.presentation.response.FeedSummaryDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class FeedBookmarkQueryService {

    private final FeedBookmarkRepository feedBookmarkRepository;
    private final FeedMediaRepository feedMediaRepository;
    private final FeedTagRepository feedTagRepository;

    public List<FeedSummaryDto> getUserBookmarkedFeeds(Long userId) {

        List<FeedBookmark> bookmarks =
                feedBookmarkRepository.findByUserIdOrderByCreatedAtDesc(userId);

        List<Long> feedIds = bookmarks.stream()
                .map(bm -> bm.getFeed().getId())
                .toList();

        // media 미리 조회
        Map<Long, List<String>> mediaMap =
                feedMediaRepository.findByFeedIdIn(feedIds)
                        .stream()
                        .collect(Collectors.groupingBy(
                                fm -> fm.getFeed().getId(),
                                Collectors.mapping(FeedMedia::getMediaUrl, Collectors.toList())
                        ));

        // tag 미리 조회
        Map<Long, List<String>> tagMap =
                feedTagRepository.findAllByFeedIdIn(feedIds)
                        .stream()
                        .collect(Collectors.groupingBy(
                                ft -> ft.getFeed().getId(),
                                Collectors.mapping(ft -> ft.getTag().getName(), Collectors.toList())
                        ));

        return bookmarks.stream()
                .map(bm -> {
                    Feed feed = bm.getFeed();
                    return FeedSummaryDto.of(
                            feed,
                            tagMap.getOrDefault(feed.getId(), List.of()),
                            mediaMap.getOrDefault(feed.getId(), List.of()),
                            false,
                            true
                    );
                })
                .toList();
    }
}
