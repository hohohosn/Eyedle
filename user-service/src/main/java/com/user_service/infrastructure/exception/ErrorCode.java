package com.user_service.infrastructure.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {

	// 400 Bad Request
	INVALID_INPUT_VALUE(HttpStatus.BAD_REQUEST, "USER001", "입력값이 올바르지 않습니다."),
	INVALID_PASSWORD(HttpStatus.BAD_REQUEST, "USER002", "비밀번호가 일치하지 않습니다."),
	DUPLICATE_EMAIL(HttpStatus.BAD_REQUEST, "USER003", "이미 사용 중인 이메일입니다."),
	DUPLICATE_USERNAME(HttpStatus.BAD_REQUEST, "USER004", "이미 사용 중인 사용자명입니다."),

	// 401 Unauthorized
	INVALID_TOKEN(HttpStatus.UNAUTHORIZED, "AUTH001", "유효하지 않은 토큰입니다."),
	EXPIRED_TOKEN(HttpStatus.UNAUTHORIZED, "AUTH002", "만료된 토큰입니다."),
	UNAUTHORIZED(HttpStatus.UNAUTHORIZED, "AUTH003", "인증이 필요합니다."),
	TOKEN_NOT_EXPIRED(HttpStatus.UNAUTHORIZED, "AUTH006", "Access Token이 아직 유효합니다."),
	REFRESH_TOKEN_NOT_FOUND(HttpStatus.UNAUTHORIZED, "AUTH007", "Refresh Token을 찾을 수 없습니다."),
	REFRESH_TOKEN_REUSED(HttpStatus.UNAUTHORIZED, "AUTH008", "이미 사용된 Refresh Token입니다."),

	// 403 Forbidden
	FORBIDDEN(HttpStatus.FORBIDDEN, "AUTH004", "권한이 없습니다."),
	ACCOUNT_NOT_ACTIVE(HttpStatus.FORBIDDEN, "USER005", "비활성화된 계정입니다."),

	// 404 Not Found
	USER_NOT_FOUND(HttpStatus.NOT_FOUND, "USER006", "사용자를 찾을 수 없습니다."),

	// 500 Internal Server Error
	INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "SERVER001", "서버 오류가 발생했습니다.");

	private final HttpStatus status;
	private final String code;
	private final String message;
}