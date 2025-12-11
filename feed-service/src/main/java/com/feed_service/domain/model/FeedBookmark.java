package com.feed_service.domain.model;

import com.common.database.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "feed_bookmarks", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"feed_id", "userId"})
        }// 복합 유니크 키(Unique Key) 제약조건을 정의
)
public class FeedBookmark extends BaseTimeEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "feed_id", nullable = false)
    private Feed feed;

    @Column(nullable = false)
    private Long userId;

    public FeedBookmark(Feed feed, Long userId) {
        this.feed = feed;
        this.userId = userId;
    }
}
