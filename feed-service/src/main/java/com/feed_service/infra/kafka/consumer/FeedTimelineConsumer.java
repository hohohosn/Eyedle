package com.feed_service.infra.kafka.consumer;

import com.feed_service.application.service.FollowQueryService;
import com.feed_service.domain.model.FeedTimeline;
import com.feed_service.domain.repository.FeedTimelineRepository;
import com.feed_service.infra.kafka.event.FeedCreatedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class FeedTimelineConsumer {

    private final FeedTimelineRepository feedTimelineRepository;
    private final FollowQueryService followQueryService;

    @KafkaListener(
            topics = "feed-created",
            groupId = "feed-timeline-group"
    )
    @Transactional
    public void consume(FeedCreatedEvent event) {

        // 1. 작성자 본인 타임라인
        feedTimelineRepository.save(
                new FeedTimeline(event.getAuthorId(), event.getFeedId())
        );

        // 2. 팔로워 타임라인
        List<Long> followerIds =
                followQueryService.getFollowers(event.getAuthorId());

        List<FeedTimeline> timelines = followerIds.stream()
                .map(followerId ->
                        new FeedTimeline(followerId, event.getFeedId())
                )
                .toList();

        feedTimelineRepository.saveAll(timelines);

        log.info("피드 팬아웃이 완료되었습니다. feedId={}", event.getFeedId());
    }
}
