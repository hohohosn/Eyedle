package com.eyedle.notification_service.presentation.dto.request;

import com.common.exception.CustomException;
import com.eyedle.notification_service.domain.model.NotificationType;
import com.eyedle.notification_service.presentation.enums.NotificationErrorCode;

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
	private String type;
	private String message;
	private Long targetId;
	private Long subTargetId;
	private String createdAt;

	public NotificationCreateRequestDto toRequestDto() {

		NotificationType enumType;
		try {
			enumType = NotificationType.valueOf(this.type);
		} catch (IllegalArgumentException | NullPointerException e) {
			throw new CustomException(NotificationErrorCode.BAD_TYPE_REQUEST);
		}

		return NotificationCreateRequestDto.builder()
			.receiverId(this.receiverId)
			.type(enumType)
			.message(this.message)
			.targetId(this.targetId)
			.subTargetId(this.subTargetId)
			.build();
	}
}
