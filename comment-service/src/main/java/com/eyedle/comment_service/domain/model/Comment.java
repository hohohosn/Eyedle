package com.eyedle.comment_service.domain.model;

import java.time.LocalDateTime;

import org.springframework.data.annotation.LastModifiedBy;

import com.common.database.BaseTimeEntity;
import com.eyedle.comment_service.domain.vo.Author;

import jakarta.persistence.AttributeOverride;
import jakarta.persistence.AttributeOverrides;
import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "p_comment")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Comment extends BaseTimeEntity {

	@Column(name = "feed_id", nullable = false)
	private Long feedId;

	@Column(name = "parent_id")
	private Long parentId;

	@Column(nullable = false, columnDefinition = "TEXT")
	private String content;

	@Embedded
	@AttributeOverrides({
		@AttributeOverride(name = "id", column = @Column(name = "created_by", nullable = false, updatable = false)),
		@AttributeOverride(name = "name", column = @Column(name = "author_name", nullable = false)),
		@AttributeOverride(name = "profileImg", column = @Column(name = "author_profile_img"))
	})
	private Author author;

	@LastModifiedBy
	@Column(name = "updated_by")
	private Long updatedBy;

	@Column(name = "deleted_at")
	private LocalDateTime deletedAt;

	@Column(name = "deleted_by")
	private Long deletedBy;

	@Builder
	public Comment(Long feedId, Long parentId, String content, Author author){
		this.feedId = feedId;
		this.parentId = parentId;
		this.content = content;
		this.author = author;
	}

	public void updateContents(String content){
		this.content = content;
	}

	public void softDelete(Long userId){
		this.deletedBy = userId;
		this.deletedAt = LocalDateTime.now();
	}

	public boolean isDeleted(){
		return this.deletedAt != null;
	}

}
