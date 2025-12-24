package com.feed_service.infra.kafka.event;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class FeedCreatedEvent {
    private Long feedId;
    private Long authorId;
    private LocalDateTime createdAt;
}
