package com.eyedle.notification_service.domain.model;

import java.time.LocalDateTime;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.PersistenceCreator;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;
import org.springframework.data.mongodb.core.index.Indexed;

import com.common.utils.TsidUtil;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Document(collection = "p_notification")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Notification {

	@Id
	private Long id;

	@Field("notification_type")
	private NotificationType notificationType;

	@Field("target_id")
	private Long targetId;

	@Field("sub_target_id")
	private Long subTargetId;

	@Indexed
	@Field("receiver_id")
	private Long receiverId;

	@Field
	private String message;

	@Field(name = "read_at")
	private LocalDateTime readAt;

	@CreatedDate
	@Field("created_at")
	private LocalDateTime createdAt;

	@Field("deleted_at")
	private LocalDateTime deletedAt;

	@PersistenceCreator
	@Builder
	public Notification(Long id, NotificationType type, Long targetId, Long subTargetId, Long receiverId, String message) {
		this.id = (id != null) ? id : TsidUtil.nextId();
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

	public boolean isNotice() { return this.notificationType == NotificationType.NOTICE; }

}
