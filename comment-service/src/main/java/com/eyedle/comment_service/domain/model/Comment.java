package com.eyedle.comment_service.domain.model;

import java.time.LocalDateTime;

import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

import com.github.f4b6a3.tsid.TsidCreator;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
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

	@Column(nullable = false, length = 1000)
	private String contents;

	@CreatedDate
	@Column(name = "created_at", nullable = false, updatable = false)
	private LocalDateTime createdAt;

	@CreatedBy
	@Column(name = "created_by", nullable = false, updatable = false)
	private Long createdBy;

	@LastModifiedDate
	@Column(name = "updated_at")
	private LocalDateTime updatedAt;

	@LastModifiedBy
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

	@PreUpdate
	public void preUpdate(){
		this.updatedAt = LocalDateTime.now();
	}

	@Builder
	public Comment(Long feedId, Long parentId, String contents){
		this.feedId = feedId;
		this.parentId = parentId;
		this.contents = contents;
	}

	public void updateContents(String content){
		this.contents = content;
	}

	public void softDelete(Long userId){
		this.deletedBy = userId;
		this.deletedAt = LocalDateTime.now();
	}

	public boolean isDeleted(){
		return this.deletedAt != null;
	}

}
