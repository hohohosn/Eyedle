package com.feed_service.domain.model;

import com.common.database.BaseTimeEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import jakarta.persistence.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "tags", indexes = {
        @Index(name = "idx_tag_name", columnList = "name"),
        @Index(name = "idx_tag_count", columnList = "count")})

public class Tag extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 50)
    private String name;

    @Column(nullable = false)
    private Long count = 0L;

    public Tag(String name) {
        this.name = name;
        this.count = 0L;
    }

    public void increaseCount() {
        this.count++;
    }

    public void decreaseCount() {
        if (this.count > 0) {
            this.count--;
        }
    }
}
