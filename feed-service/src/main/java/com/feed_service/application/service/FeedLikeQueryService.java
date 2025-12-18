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

    private final FeedLikeRepository feedLikeRepository;

    public List<FeedSummaryDto> getUserLikedFeeds(@RequestHeader("X-User-Id") Long userId) {

        List<FeedLike> likes = feedLikeRepository.findByUserIdOrderByCreatedAtDesc(userId);

        return likes.stream()
                .map(like -> FeedSummaryDto.from(
                    like.getFeed(),
                        true,
                        false // 나중에 고도화 예정
                ))
                .toList();
    }
}
