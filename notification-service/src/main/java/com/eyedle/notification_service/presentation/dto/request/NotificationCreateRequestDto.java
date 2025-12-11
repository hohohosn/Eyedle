package com.eyedle.notification_service.presentation.dto.request;

import java.util.List;

import com.common.utils.TsidUtil;
import com.eyedle.notification_service.domain.model.Notification;
import com.eyedle.notification_service.domain.model.NotificationType;

import jakarta.validation.constraints.NotEmpty;
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

	@NotEmpty(message = "알림 수신자는 필수입니다.")
	private List<Long> receiverIds;

	@NotNull(message = "알림 타입은 필수입니다.")
	private NotificationType type;

	private String message;

	@NotNull(message = "알림 대상은 필수입니다.")
	private Long targetId;

	private Long subTargetId;

	private String sender;

	public List<Notification> toEntities(String message) {
		return receiverIds.stream()
			.map(receiverId -> toEntity(receiverId, message, this.type))
			.toList();
	}

	private Notification toEntity(Long receiverId, String message, NotificationType type) {
		return Notification.builder()
			.receiverId(receiverId)
			.notificationType(type)
			.message(message)
			.targetId(this.targetId)
			.subTargetId(this.subTargetId)
			.build();
	}

}
