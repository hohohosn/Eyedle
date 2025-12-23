package com.feed_service.domain.model;

import com.common.database.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "p_feed")
@Getter
@NoArgsConstructor
@EntityListeners(AuditingEntityListener.class)
@SQLDelete(sql = "UPDATE p_feed SET deleted = true, deleted_at = now() WHERE id = ?")
@SQLRestriction("deleted = false")
public class Feed extends BaseTimeEntity {

    @Column(nullable = false)
    private Long userId;

    @Column(columnDefinition = "TEXT")
    private String content;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private FeedPermission permission;

    @Column(nullable = false)
    private boolean deleted;

    private LocalDateTime deletedAt;

    @OneToMany(mappedBy = "feed", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private Set<FeedMedia> mediaList = new LinkedHashSet<>();

    @OneToMany(mappedBy = "feed", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private Set<FeedTag> feedTags = new LinkedHashSet<>();

    @Builder
    public Feed(Long userId, String content, FeedPermission permission) {
        this.userId = userId;
        this.content = content;
        this.permission = permission;
        this.deleted = false;
    }

    public void updateFeed(String content, FeedPermission permission) {
        this.content = content;
        this.permission = permission;
    }

    public void statusDeleted() {
        this.deleted = true;
        this.deletedAt = LocalDateTime.now();
    }
}
