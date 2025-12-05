package com.eyedle.notification_service.presentation.dto.response;

import java.time.LocalDateTime;

import com.eyedle.notification_service.domain.model.Notification;
import com.eyedle.notification_service.domain.model.NotificationType;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NotificationCreateResponseDto {

	private Long id;
	private Long receiverId;
	private NotificationType type;
	private String message;
	private Long targetId;
	private Long subTargetId;
	private LocalDateTime createdAt;

	public static NotificationCreateResponseDto fromEntity(Notification notification) {
		return NotificationCreateResponseDto.builder()
			.id(notification.getId())
			.receiverId(notification.getReceiverId())
			.type(notification.getNotificationType())
			.message(notification.getMessage())
			.targetId(notification.getTargetId())
			.subTargetId(notification.getSubTargetId())
			.createdAt(notification.getCreatedAt())
			.build();
	}

}
