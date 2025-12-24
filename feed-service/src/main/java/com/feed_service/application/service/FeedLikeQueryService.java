package com.feed_service.application.service;

import com.feed_service.domain.model.Feed;
import com.feed_service.domain.model.FeedLike;
import com.feed_service.domain.model.FeedMedia;
import com.feed_service.domain.repository.FeedLikeRepository;
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
public class FeedLikeQueryService {

    private final FeedLikeRepository feedLikeRepository;
    private final FeedMediaRepository feedMediaRepository;
    private final FeedTagRepository feedTagRepository;

    public List<FeedSummaryDto> getUserLikedFeeds(Long userId) {

        List<FeedLike> likes =
                feedLikeRepository.findByUserIdOrderByCreatedAtDesc(userId);

        List<Long> feedIds = likes.stream()
                .map(like -> like.getFeed().getId())
                .toList();

        Map<Long, List<String>> mediaMap =
                feedMediaRepository.findByFeedIdIn(feedIds)
                        .stream()
                        .collect(Collectors.groupingBy(
                                fm -> fm.getFeed().getId(),
                                Collectors.mapping(FeedMedia::getMediaUrl, Collectors.toList())
                        ));

        Map<Long, List<String>> tagMap =
                feedTagRepository.findAllByFeedIdIn(feedIds)
                        .stream()
                        .collect(Collectors.groupingBy(
                                ft -> ft.getFeed().getId(),
                                Collectors.mapping(ft -> ft.getTag().getName(), Collectors.toList())
                        ));

        return likes.stream()
                .map(like -> {
                    Feed feed = like.getFeed();
                    return FeedSummaryDto.of(
                            feed,
                            tagMap.getOrDefault(feed.getId(), List.of()),
                            mediaMap.getOrDefault(feed.getId(), List.of()),
                            true,
                            false
                    );
                })
                .toList();
    }
}
