package com.user_service.presentation.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LogoutResponse {

	/**
	 * 로그아웃 메시지
	 */
	private String message;

	/**
	 * 더미 액세스 토큰 (기존 토큰을 덮어씀)
	 */
	private String dummyAccessToken;
}