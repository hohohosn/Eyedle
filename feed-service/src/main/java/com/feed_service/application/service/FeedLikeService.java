package com.feed_service.application.service;

import com.feed_service.domain.model.FeedLike;
import com.feed_service.domain.repository.FeedLikeRepository;
import com.feed_service.domain.repository.FeedRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Transactional
public class FeedLikeService {

    private final FeedLikeRepository feedLikeRepository;

    public void createLike(Long feedId, Long userId) {
        boolean exists = feedLikeRepository.existsByFeedIdAndUserId(feedId, userId);
        if (exists) throw new IllegalArgumentException("이미 좋아요 눌렀습니다.");

        FeedLike like = new FeedLike(feedId, userId);
        feedLikeRepository.save(like);
    }

    @Transactional
    public void deleteLike(Long feedId, Long userId) {
        feedLikeRepository.deleteByFeedIdAndUserId(feedId, userId);
    }
}
