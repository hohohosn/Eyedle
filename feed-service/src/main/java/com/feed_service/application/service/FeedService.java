package com.feed_service.application.service;

import com.feed_service.domain.model.Feed;
import com.feed_service.domain.repository.FeedRepository;
import com.feed_service.presentation.request.FeedCreateRequestDto;
import com.feed_service.presentation.response.FeedResponseDto;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
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
}
