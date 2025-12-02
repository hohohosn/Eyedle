package com.feed_service.application.service;

import com.feed_service.domain.model.Feed;
import com.feed_service.domain.repository.FeedRepository;
import com.feed_service.presentation.request.FeedCreateRequestDto;
import com.feed_service.presentation.request.FeedUpdateRequestDto;
import com.feed_service.presentation.response.FeedResponseDto;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class FeedService {

    private final FeedRepository feedRepository;

    @Transactional
    public FeedResponseDto createFeed(FeedCreateRequestDto request, Long userId, Long feedId) {
        Feed feed = request.toEntity(userId, feedId);
        feed = feedRepository.save(feed);
        return FeedResponseDto.mapFeed(feed);

    }
    @Transactional
    public FeedResponseDto findFeed(Long feedId) {
        Feed feed = feedRepository.findById(feedId)
                .orElseThrow(() -> new IllegalArgumentException("Feed Not Found!"));
        return FeedResponseDto.mapFeed(feed);
    }

    public Page<FeedResponseDto> findAllFeeds(Pageable pageable) {
        Page<Feed> feeds = feedRepository.findFeeds(pageable);
        return feeds.map(FeedResponseDto::mapFeed);
    }

    @Transactional
    public FeedResponseDto updateFeed(Long feedId, FeedUpdateRequestDto request) {
        Feed feed = feedRepository.findById(feedId)
                .orElseThrow(() -> new IllegalArgumentException("Feed Not Found!"));
        feed.updateFeed(request.getContent(), request.getPermission());
        return FeedResponseDto.mapFeed(feed);
    }

    @Transactional
    public void statusDeleted(Long feedId) {
        Feed feed = feedRepository.findById(feedId)
                .orElseThrow(() -> new IllegalArgumentException("Feed Not Found!"));
        feed.statusDeleted();
    }

}
