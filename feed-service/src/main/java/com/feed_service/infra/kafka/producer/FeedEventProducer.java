package com.feed_service.infra.kafka.producer;

import com.feed_service.infra.kafka.event.FeedCreatedEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class FeedEventProducer {

    private final KafkaTemplate<String, FeedCreatedEvent> kafkaTemplate;

    public void publishFeedEvent(FeedCreatedEvent event) {
        kafkaTemplate.send("feed-created", event);
    }
}
