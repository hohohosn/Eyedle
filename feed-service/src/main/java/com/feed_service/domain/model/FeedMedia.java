package com.feed_service.domain.model;

import com.common.database.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = lombok.AccessLevel.PROTECTED)
@Entity
@Table(name = "p_feed_media")
public class FeedMedia extends BaseTimeEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "feed_id", nullable = false)
    private Feed feed;

    @Column(nullable = false)
    private String mediaUrl;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private MediaType mediaType;

    public FeedMedia(Feed feed, String mediaUrl, MediaType mediaType) {
        this.feed = feed;
        this.mediaUrl = mediaUrl;
        this.mediaType = mediaType;
    }
}
