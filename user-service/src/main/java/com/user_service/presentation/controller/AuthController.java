package com.user_service.presentation.controller;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.user_service.application.service.AuthService;
import com.user_service.presentation.dto.request.LoginRequest;
import com.user_service.presentation.dto.request.SignupRequest;
import com.user_service.presentation.dto.response.ApiResponse;
import com.user_service.presentation.dto.response.AuthResponse;
import com.user_service.presentation.dto.response.LogoutResponse;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

	private final AuthService authService;

	/**
	 * 회원가입
	 */
	@PostMapping("/signup")
	public ResponseEntity<ApiResponse<Void>> signup(
		@Valid @RequestBody SignupRequest request
	) {
		log.info("회원가입 요청: email={}", request.getEmail());

		AuthResponse authResponse = authService.signup(request);

		HttpHeaders headers = new HttpHeaders();
		headers.set("Authorization", "Bearer " + authResponse.getAccessToken());
		headers.set("X-Refresh-Token", authResponse.getRefreshToken());

		return ResponseEntity
			.status(HttpStatus.CREATED)
			.headers(headers)
			.body(ApiResponse.success("회원가입이 완료되었습니다."));
	}

	/**
	 * 로그인(토큰을 Header로 반환
	 */
	@PostMapping("/login")
	public ResponseEntity<ApiResponse<Void>> login(
		@Valid @RequestBody LoginRequest request
	) {
		log.info("로그인 요청: email={}", request.getEmail());

		AuthResponse authResponse = authService.login(request);

		HttpHeaders headers = new HttpHeaders();
		headers.set("Authorization", "Bearer " + authResponse.getAccessToken());
		headers.set("X-Refresh-Token", authResponse.getRefreshToken());

		return ResponseEntity
			.ok()
			.headers(headers)
			.body(ApiResponse.success("로그인에 성공했습니다."));
	}

	/**
	 * 토큰 갱신
	 */
	@PostMapping("/refresh")
	public ResponseEntity<ApiResponse<Void>> refreshToken(
		@RequestHeader("X-Refresh-Token") String refreshToken
	) {
		log.info("토큰 갱신 요청");

		AuthResponse authResponse = authService.refreshToken(refreshToken);

		HttpHeaders headers = new HttpHeaders();
		headers.set("Authorization", "Bearer " + authResponse.getAccessToken());
		headers.set("X-Refresh-Token", authResponse.getRefreshToken());

		return ResponseEntity
			.ok()
			.headers(headers)
			.body(ApiResponse.success("토큰이 갱신되었습니다."));
	}

	/**
	 * 로그아웃(더미 액세스 토큰으로 기존 토큰 무효화)
	 */
	@PostMapping("/logout")
	public ResponseEntity<ApiResponse<Void>> logout(
		@AuthenticationPrincipal Long userId
	) {
		log.info("로그아웃 요청: userId={}", userId);

		LogoutResponse logoutResponse = authService.logout(userId);

		HttpHeaders headers = new HttpHeaders();
		headers.set("Authorization", "Bearer " + logoutResponse.getDummyAccessToken());
		// Refresh Token은 삭제되었으므로 빈 문자열 반환
		headers.set("X-Refresh-Token", "");

		return ResponseEntity
			.ok()
			.headers(headers)
			.body(ApiResponse.success(logoutResponse.getMessage()));
	}
}