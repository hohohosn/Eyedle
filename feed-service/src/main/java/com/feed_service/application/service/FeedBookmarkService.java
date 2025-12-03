package com.feed_service.application.service;

import com.feed_service.domain.model.FeedBookmark;
import com.feed_service.domain.repository.FeedBookmarkRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
@Service
@RequiredArgsConstructor
public class FeedBookmarkService {

    private final FeedBookmarkRepository feedBookmarkRepository;

    @Transactional
    public void bookmarkFeed(Long feedId, Long userId){
        boolean exists = feedBookmarkRepository.existsByFeedIdAndUserId(feedId, userId);
        if(exists) throw new IllegalStateException("이미 저장한 게시글");

        feedBookRepository.save(new FeedBookmark(feedId, userId));
    }

    @Transactional
    public void deleteBookmarkFeed(Long feedId, Long userId){
        feedBookRepository.deleteByFeedIdAndUserId(feedId, userId);
    }
}
