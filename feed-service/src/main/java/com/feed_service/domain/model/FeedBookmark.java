package com.feed_service.domain.model;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "feed_bookmarks")
public class FeedBookmark {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long feedBookmarkId;

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
