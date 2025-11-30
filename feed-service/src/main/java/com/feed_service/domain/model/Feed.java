package com.feed_service.domain.model;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "p_feed")
@Getter
@NoArgsConstructor
public class Feed {

    @Id
    private Long id; // TSID

    @Column(nullable = false)
    private Long userId;

    @Column(columnDefinition = "TEXT")
    private String content;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private FeedPermission permission;

    private boolean isDeleted;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime deletedAt;

    @Builder
    public Feed(Long id, Long userId, String content, FeedPermission permission,
                boolean isDeleted, LocalDateTime createdAt, LocalDateTime updatedAt, LocalDateTime deletedAt) {
        this.id = id;
        this.userId = userId;
        this.content = content;
        this.permission = permission;
        this.isDeleted = isDeleted;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.deletedAt = deletedAt;
    }

    // 수정
    public void updateFeed(String content, FeedPermission permission) {
        this.content = content;
        this.permission = permission;
        this.updatedAt = LocalDateTime.now();
    }

    // 논리삭제
    public void statusDeleted() {
        this.isDeleted = true;
        this.deletedAt = LocalDateTime.now();
    }
}
