package com.feed_service.application.service;

import com.common.exception.CustomException;
import com.common.response.ErrorCode;
import com.feed_service.domain.model.Feed;
import com.feed_service.domain.model.FeedLike;
import com.feed_service.domain.repository.FeedLikeRepository;
import com.feed_service.domain.repository.FeedRepository;
import com.feed_service.presentation.response.FeedLikeResponseDto;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Transactional
public class FeedLikeService {

    private final FeedRepository feedRepository;
    private final FeedLikeRepository feedLikeRepository;

    public FeedLikeResponseDto toggleLike(Long feedId, Long userId) {

        Feed feed = feedRepository.findById(feedId)
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND));

        boolean exists = feedLikeRepository.existsByFeed_IdAndUserId(feedId, userId);

        if(exists) {
            feedLikeRepository.deleteByFeed_IdAndUserId(feedId, userId);
        } else{
            feedLikeRepository.save(new FeedLike(feed, userId));
        }

        long likeCount = feedLikeRepository.countByFeed_Id(feedId);

        return new FeedLikeResponseDto(!exists, likeCount);
    }
}
