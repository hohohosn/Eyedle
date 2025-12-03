package com.eyedle.notification_service.domain.model;

import java.time.LocalDateTime;

import org.springframework.data.annotation.CreatedDate;

import com.common.database.BaseRootEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Table(name = "p_notification")
public class Notification extends BaseRootEntity {

	@Enumerated(EnumType.STRING)
	@Column(name = "notification_type", nullable = false)
	private NotificationType notificationType;

	@Column(name = "target_id", nullable = false)
	private Long targetId;

	@Column(name = "sub_target_id")
	private Long subTargetId;

	@Column(name = "receiver_id", nullable = false)
	private Long receiverId;

	@Column(columnDefinition = "TEXT", nullable = false)
	private String message;

	@Column(name = "read_at")
	private LocalDateTime readAt;

	@CreatedDate
	@Column(name = "created_at", nullable = false, updatable = false)
	private LocalDateTime createdAt;

	@Column(name = "deleted_at")
	private LocalDateTime deletedAt;

	@Builder
	public Notification(NotificationType type
		, Long targetId
		, Long subTargetId
		, Long receiverId
		, String message) {
		this.notificationType = type;
		this.targetId = targetId;
		this.subTargetId = subTargetId;
		this.receiverId = receiverId;
		this.message = message;
	}

	public void markAsRead() {
		this.readAt = LocalDateTime.now();
	}

	public void softDelete() {
		this.deletedAt = LocalDateTime.now();
	}

}
