package com.feed_service.domain.model;

import com.common.database.BaseTimeEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
@Table(name = "p_tag")
public class Tag extends BaseTimeEntity {

    @Column(nullable = false, unique = true)
    private String name;

    @Column(nullable = false)
    private long count = 0;

    public Tag(String name) {
        this.name = name.toLowerCase();
    }

    public void increaseCount() {
        this.count++;
    }

}
