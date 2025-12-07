package com.user_service.presentation.controller;

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
import com.user_service.presentation.dto.request.RefreshTokenRequest;
import com.user_service.presentation.dto.request.SignupRequest;
import com.user_service.presentation.dto.response.ApiResponse;
import com.user_service.presentation.dto.response.AuthResponse;

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
	public ResponseEntity<ApiResponse<AuthResponse>> signup(
		@Valid @RequestBody SignupRequest request
	) {
		log.info("회원가입 요청: email={}", request.getEmail());

		AuthResponse response = authService.signup(request);

		return ResponseEntity
			.status(HttpStatus.CREATED)
			.body(ApiResponse.success("회원가입이 완료되었습니다.", response));
	}

	/**
	 * 로그인
	 */
	@PostMapping("/login")
	public ResponseEntity<ApiResponse<AuthResponse>> login(
		@Valid @RequestBody LoginRequest request
	) {
		log.info("로그인 요청: email={}", request.getEmail());

		AuthResponse response = authService.login(request);

		return ResponseEntity.ok(
			ApiResponse.success("로그인에 성공했습니다.", response)
		);
	}

	/**
	 * 토큰 갱신 (보안 강화 버전)
	 */
	@PostMapping("/refresh")
	public ResponseEntity<ApiResponse<AuthResponse>> refreshToken(
		@Valid @RequestBody RefreshTokenRequest request
	) {
		log.info("토큰 갱신 요청");
		AuthResponse response = authService.refreshToken(request);
		return ResponseEntity.ok(ApiResponse.success("토큰이 갱신되었습니다.", response));
	}

	/**
	 * 로그아웃
	 */
	@PostMapping("/logout")
	public ResponseEntity<ApiResponse<String>> logout(
		@AuthenticationPrincipal Long userId
	) {
		log.info("로그아웃 요청: userId={}", userId);
		authService.logout(userId);
		return ResponseEntity.ok(ApiResponse.success("로그아웃이 완료되었습니다."));
	}

}
