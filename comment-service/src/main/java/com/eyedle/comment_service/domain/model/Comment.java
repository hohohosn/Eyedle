package com.eyedle.comment_service.domain.model;

import java.time.LocalDateTime;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import com.eyedle.comment_service.domain.vo.Author;
import com.github.f4b6a3.tsid.TsidCreator;

import jakarta.persistence.AttributeOverride;
import jakarta.persistence.AttributeOverrides;
import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "p_comment")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EntityListeners({AuditingEntityListener.class})
public class Comment {
	@Id
	@Column(name = "comment_id")
	private Long commentId;

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

	@CreatedDate
	@Column(name = "created_at", nullable = false, updatable = false)
	private LocalDateTime createdAt;

	@LastModifiedDate
	@Column(name = "updated_at")
	private LocalDateTime updatedAt;

	@Column(name = "updated_by")
	private Long updatedBy;

	@Column(name = "deleted_at")
	private LocalDateTime deletedAt;

	@Column(name = "deleted_by")
	private Long deletedBy;

	@PrePersist
	public void prePersist(){
		if (this.commentId == null){
			this.commentId = TsidCreator.getTsid().toLong();
		}
	}

	@Builder
	public Comment(Long feedId, Long parentId, String content, Author author){
		this.feedId = feedId;
		this.parentId = parentId;
		this.content = content;
		this.author = author;
	}

	public void updateContents(String content, Long userId){
		this.content = content;
		this.updatedBy = userId;
	}

	public void softDelete(Long userId){
		this.deletedBy = userId;
		this.deletedAt = LocalDateTime.now();
	}

	public boolean isDeleted(){
		return this.deletedAt != null;
	}

}
