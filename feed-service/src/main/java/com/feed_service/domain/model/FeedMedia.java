package com.feed_service.domain.model;

import com.common.database.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Getter
@NoArgsConstructor(access = lombok.AccessLevel.PROTECTED)
@Entity
@Table(name = "p_feed_media")
@EntityListeners(AuditingEntityListener.class)
public class FeedMedia extends BaseTimeEntity {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "feed_id", nullable = false)
    private Feed feed;

    @Column(nullable = false)
    private String mediaUrl;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private MediaType mediaType;

    @Column(nullable = false)
    private int orderIndex;

    public FeedMedia(Feed feed, String mediaUrl, MediaType mediaType, int orderIndex) {
        this.feed = feed;
        this.mediaUrl = mediaUrl;
        this.mediaType = mediaType;
        this.orderIndex = orderIndex;
    }
}
