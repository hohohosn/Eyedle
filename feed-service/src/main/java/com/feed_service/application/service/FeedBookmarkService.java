package com.feed_service.application.service;

import com.common.exception.CustomException;
import com.common.response.ErrorCode;
import com.common.response.SuccessCode;
import com.feed_service.domain.model.Feed;
import com.feed_service.domain.model.FeedBookmark;
import com.feed_service.domain.repository.FeedBookmarkRepository;
import com.feed_service.domain.repository.FeedRepository;
import com.feed_service.presentation.response.FeedBookmarkResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestHeader;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class FeedBookmarkService {

    private final FeedRepository feedRepository;
    private final FeedBookmarkRepository feedBookmarkRepository;

    public FeedBookmarkResponseDto toggleBookmark(Long feedId, @RequestHeader("X-User-Id") Long userId) {

        Feed feed = feedRepository.findById(feedId)
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND));

        boolean exists = feedBookmarkRepository.existsByFeed_IdAndUserId(feedId, userId);

        if (exists) {
            feedBookmarkRepository.deleteByFeed_IdAndUserId(feedId, userId);
        } else{
            feedBookmarkRepository.save(new FeedBookmark(feed, userId));
        }

        return new FeedBookmarkResponseDto(!exists);
    }
}
