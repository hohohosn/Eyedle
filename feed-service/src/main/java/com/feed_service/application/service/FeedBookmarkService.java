package com.feed_service.application.service;

import com.common.exception.CustomException;
import com.common.response.ErrorCode;
import com.feed_service.domain.model.Feed;
import com.feed_service.domain.model.FeedBookmark;
import com.feed_service.domain.repository.FeedBookmarkRepository;
import com.feed_service.domain.repository.FeedRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
@Service
@RequiredArgsConstructor
public class FeedBookmarkService {

    private final FeedRepository feedRepository;
    private final FeedBookmarkRepository feedBookmarkRepository;

    @Transactional
    public void bookmarkFeed(Long feedId, Long userId){

        Feed feed = feedRepository.findById(feedId)
                .orElseThrow(() -> new CustomException(ErrorCode.CONFLICT));

        if(feedBookmarkRepository.existsByFeed_IdAndUserId(feedId, userId)){
            throw new CustomException(ErrorCode.NOT_FOUND);
        }

        feedBookmarkRepository.save(new  FeedBookmark(feed, userId));
    }

    @Transactional
    public void deleteBookmarkFeed(Long feedId, Long userId){
        feedBookmarkRepository.deleteByFeed_IdAndUserId(feedId, userId);
    }
}
