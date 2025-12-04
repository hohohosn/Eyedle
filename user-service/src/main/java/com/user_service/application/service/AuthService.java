package com.user_service.application.service;

import com.user_service.domain.model.User;
import com.user_service.domain.repository.UserRepository;
import com.user_service.infrastructure.exception.BusinessException;
import com.user_service.infrastructure.exception.ErrorCode;
import com.user_service.infrastructure.security.JwtTokenProvider;
import com.user_service.presentation.dto.request.LoginRequest;
import com.user_service.presentation.dto.request.SignupRequest;
import com.user_service.presentation.dto.response.AuthResponse;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AuthService {

	private final UserRepository userRepository;
	private final PasswordEncoder passwordEncoder;
	private final JwtTokenProvider jwtTokenProvider;

	/**
	 * 회원가입
	 */
	@Transactional
	public AuthResponse signup(SignupRequest request) {
		log.info("회원가입 : email={}", request.getEmail());

		// 이메일 중복 체크
		if (userRepository.existsByEmail(request.getEmail())) {
			throw new BusinessException(ErrorCode.DUPLICATE_EMAIL);
		}

		// 사용자명 중복 체크
		if (userRepository.existsByUsername(request.getUsername())) {
			throw new BusinessException(ErrorCode.DUPLICATE_USERNAME);
		}

		// 비밀번호 암호화 PasswordEncoder
		String encodedPassword = passwordEncoder.encode(request.getPassword());

		// 사용자 생성 및 저장
		User user = User.createUser(
			request.getEmail(),
			request.getUsername(),
			encodedPassword,
			"SYSTEM"
		);
		User savedUser = userRepository.save(user);

		// JWT 토큰 생성
		String accessToken = jwtTokenProvider.generateAccessToken(
			savedUser.getId(),
			savedUser.getEmail(),
			savedUser.getRole().getKey()
		);
		String refreshToken = jwtTokenProvider.generateRefreshToken(savedUser.getId());

		log.info("회원가입 완료: userId={}, email={}", savedUser.getId(), savedUser.getEmail());

		// 응답 반환
		return AuthResponse.of(savedUser, accessToken, refreshToken);
	}

	// 로그인
	@Transactional
	public AuthResponse login(LoginRequest request) {
		log.info("로그인 시도: email={}", request.getEmail());

		// 사용자 조회
		User user = userRepository.findByEmail(request.getEmail())
			.orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

		// 계정 상태 확인
		if (!user.isActive()) {
			throw new BusinessException(ErrorCode.ACCOUNT_NOT_ACTIVE);
		}

		// 비밀번호 검증
		if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
			throw new BusinessException(ErrorCode.INVALID_PASSWORD);
		}

		// JWT 토큰 생성
		String accessToken = jwtTokenProvider.generateAccessToken(
			user.getId(),
			user.getEmail(),
			user.getRole().getKey()
		);
		String refreshToken = jwtTokenProvider.generateRefreshToken(user.getId());

		log.info("로그인 성공: userId={}, email={}", user.getId(), user.getEmail());

		return AuthResponse.of(user, accessToken, refreshToken);
	}

	// 토큰 재발급
	@Transactional
	public AuthResponse refreshToken(String refreshToken) {
		log.info("토큰 갱신 시도");

		// Refresh token 검증
		if (!jwtTokenProvider.validateToken(refreshToken)) {
			throw new BusinessException(ErrorCode.INVALID_TOKEN);
		}

		// 사용자 ID 추출
		Long userId = jwtTokenProvider.getUserIdFromToken(refreshToken);

		// 사용자 조회
		User user = userRepository.findById(userId)
			.orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

		// 계정 상태 확인
		if (!user.isActive()) {
			throw new BusinessException(ErrorCode.ACCOUNT_NOT_ACTIVE);
		}

		// 새 토큰 발급
		String newAccessToken = jwtTokenProvider.generateAccessToken(
			user.getId(),
			user.getEmail(),
			user.getRole().getKey()
		);
		String newRefreshToken = jwtTokenProvider.generateRefreshToken(user.getId());

		log.info("토큰 갱신 완료: userId={}", userId);

		return AuthResponse.of(user, newAccessToken, newRefreshToken);
	}
}
