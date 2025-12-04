package com.feed_service.application.service;

import com.common.exception.CustomException;
import com.common.response.ErrorCode;
import com.feed_service.domain.model.Feed;
import com.feed_service.domain.model.FeedLike;
import com.feed_service.domain.repository.FeedLikeRepository;
import com.feed_service.domain.repository.FeedRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Transactional
public class FeedLikeService {

    private final FeedRepository feedRepository;
    private final FeedLikeRepository feedLikeRepository;

    public void createLike(Long feedId, Long userId) {

        Feed feed = feedRepository.findById(feedId)
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND));

        if(feedLikeRepository.existsByFeed_IdAndUserId(feedId, userId)){
            throw new CustomException(ErrorCode.CONFLICT);
        }

        feedLikeRepository.save(new FeedLike(feed, userId));
    }

    @Transactional
    public void deleteLike(Long feedId, Long userId) {
        feedLikeRepository.deleteByFeed_IdAndUserId(feedId, userId);
    }
}
