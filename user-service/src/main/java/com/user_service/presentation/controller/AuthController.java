package com.user_service.presentation.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
	 * 토큰 갱신
	 */
	@PostMapping("/refresh")
	public ResponseEntity<ApiResponse<AuthResponse>> refresh(
		@RequestHeader("Authorization") String refreshToken
	) {
		log.info("토큰 갱신 요청");

		// Bearer 제거
		String token = refreshToken.replace("Bearer ", "");

		AuthResponse response = authService.refreshToken(token);

		return ResponseEntity.ok(
			ApiResponse.success("토큰이 갱신되었습니다.", response)
		);
	}
	
}
