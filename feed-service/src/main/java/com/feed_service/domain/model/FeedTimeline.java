package com.feed_service.domain.model;

import com.common.database.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "feed_timeline", indexes = {
                @Index(name = "idx_timeline_user_created", columnList = "user_id, created_at DESC")
        })
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class FeedTimeline extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;   // 타임라인 주인

    @Column(name = "feed_id", nullable = false)
    private Long feedId;   // 노출될 피드

    public FeedTimeline(Long userId, Long feedId) {
        this.userId = userId;
        this.feedId = feedId;
    }
}
