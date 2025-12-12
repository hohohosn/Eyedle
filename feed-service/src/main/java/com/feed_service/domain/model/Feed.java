package com.feed_service.domain.model;

import com.common.database.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Where;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "p_feed")
@Getter
@Where(clause = "is_deleted = false")
@NoArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class Feed extends BaseTimeEntity {

    @Column(nullable = false)
    private Long userId;

    @Column(columnDefinition = "TEXT")
    private String content;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private FeedPermission permission;

    @Column(nullable = false)
    private boolean isDeleted;

    private LocalDateTime deletedAt;

    @OneToMany(mappedBy = "feed", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<FeedMedia> mediaList =  new ArrayList<>();

    @OneToMany(mappedBy = "feed", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<FeedTag> feedTags = new ArrayList<>();

    @Builder
    public Feed(Long userId, String content, FeedPermission permission) {
        this.userId = userId;
        this.content = content;
        this.permission = permission;
    }

    public void updateFeed(String content, FeedPermission permission) {
        this.content = content;
        this.permission = permission;
    }

    public void statusDeleted() {
        this.isDeleted = true;
        this.deletedAt = LocalDateTime.now();
    }
}
