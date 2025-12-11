package com.feed_service.application.service;

import com.feed_service.domain.model.FeedLike;
import com.feed_service.domain.repository.FeedLikeRepository;
import com.feed_service.presentation.response.FeedSummaryDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class FeedLikeQueryService {

    private final FeedLikeService feedLikeService;
    private final FeedLikeRepository feedLikeRepository;

    public List<FeedSummaryDto> getUserLikedFeeds(Long userId) {

        List<FeedLike> likes = feedLikeRepository.findByUserIdOrderByCreatedAtDesc(userId);

        return likes.stream()
                .map(like -> FeedSummaryDto.from(
                    like.getFeed(),
                        true,
                        false
                ))
                .toList();
    }
}
