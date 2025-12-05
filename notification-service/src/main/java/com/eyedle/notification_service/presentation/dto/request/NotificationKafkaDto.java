package com.eyedle.notification_service.presentation.dto.request;

import com.eyedle.notification_service.domain.model.NotificationType;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class NotificationKafkaDto {
	private Long id;           // 알림 ID
	private Long receiverId;   // 수신자 ID
	private NotificationType type;
	private String message;
	private Long targetId;
	private Long subTargetId;
	private String createdAt;

	public NotificationCreateRequestDto toRequestDto() {
		return NotificationCreateRequestDto.builder()
			.receiverId(this.receiverId)
			.type(this.type)
			.message(this.message)
			.targetId(this.targetId)
			.subTargetId(this.subTargetId)
			.build();
	}
}
