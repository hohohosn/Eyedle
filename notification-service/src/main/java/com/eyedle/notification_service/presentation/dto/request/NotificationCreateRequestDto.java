package com.eyedle.notification_service.presentation.dto.request;

import com.eyedle.notification_service.domain.model.Notification;
import com.eyedle.notification_service.domain.model.NotificationType;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NotificationCreateRequestDto {

	@NotNull(message = "알림 수신자는 필수입니다.")
	private Long receiverId;

	@NotNull(message = "알림 타입은 필수입니다.")
	private NotificationType type;

	@NotNull(message = "알림 메세지는 필수입니다.")
	private String message;

	@NotNull(message = "알림 대상은 필수입니다.")
	private Long targetId;

	private Long subTargetId;

	public Notification toEntity() {
		return Notification.builder()
			.receiverId(this.receiverId)
			.type(this.type)
			.message(this.message)
			.targetId(this.targetId)
			.subTargetId(this.subTargetId)
			.build();
	}

}
