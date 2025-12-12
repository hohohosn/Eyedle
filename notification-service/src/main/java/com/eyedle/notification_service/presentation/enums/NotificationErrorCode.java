package com.eyedle.notification_service.presentation.enums;

import com.common.response.BaseErrorCode;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum NotificationErrorCode implements BaseErrorCode {
	BAD_TYPE_REQUEST(400, "E400", "지원하지 않는 알림입니다."),
	NOTIFICATION_FORBIDDEN(403, "E403", "해당 알림에 대한 권한이 없습니다."),
	NOTIFICATION_NOT_FOUND(404, "E404", "해당 알림을 찾을 수 없습니다.");

	private final int status;
	private final String code;
	private final String message;

}
