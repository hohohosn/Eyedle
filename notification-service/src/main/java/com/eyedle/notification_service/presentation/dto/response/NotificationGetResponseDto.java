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
public class NotificationGetResponseDto {

	private Long id;
	private Long receiverId;
	private String message;
	private NotificationType type;
	private Long targetId;
	private Long subTargetId;
	private LocalDateTime createdAt;
	private LocalDateTime readAt;

	public static NotificationGetResponseDto fromEntity(Notification notification) {
		return NotificationGetResponseDto.builder()
			.id(notification.getId())
			.receiverId(notification.getReceiverId())
			.message(notification.getMessage())
			.type(notification.getNotificationType())
			.targetId(notification.getTargetId())
			.subTargetId(notification.getSubTargetId())
			.createdAt(notification.getCreatedAt())
			.readAt(notification.getReadAt())
			.build();
	}

}
