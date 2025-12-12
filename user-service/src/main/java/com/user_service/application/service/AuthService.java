package com.user_service.application.service;

import com.user_service.domain.model.User;
import com.user_service.domain.repository.UserRepository;
import com.user_service.infrastructure.exception.BusinessException;
import com.user_service.infrastructure.exception.ErrorCode;
import com.user_service.infrastructure.security.JwtTokenProvider;
import com.user_service.presentation.dto.request.LoginRequest;
import com.user_service.presentation.dto.request.SignupRequest;
import com.user_service.presentation.dto.response.AuthResponse;
import com.user_service.presentation.dto.response.LogoutResponse;

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
	private final RefreshTokenService refreshTokenService;

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

		// 비밀번호 암호화
		String encodedPassword = passwordEncoder.encode(request.getPassword());

		// 사용자 생성 및 저장
		User user = User.createUser(
			request.getEmail(),
			request.getUsername(),
			encodedPassword
		);
		User savedUser = userRepository.save(user);

		log.info("회원가입 완료: userId={}, email={}", savedUser.getId(), savedUser.getEmail());

		return generateTokenResponse(savedUser);
	}

	/**
	 * 로그인
	 */
	@Transactional
	public AuthResponse login(LoginRequest request) {
		log.info("로그인 시도: email={}", request.getEmail());

		// 사용자 조회 및 계정 상태 확인
		User user = findActiveUserByEmail(request.getEmail());

		// 비밀번호 검증
		if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
			throw new BusinessException(ErrorCode.INVALID_PASSWORD);
		}

		log.info("로그인 성공: userId={}, email={}", user.getId(), user.getEmail());

		return generateTokenResponse(user);
	}

	/**
	 * 토큰 갱신
	 */
	@Transactional
	public AuthResponse refreshToken(String refreshToken) {
		log.info("토큰 갱신 시도");

		//Refresh Token 통합 검증
		jwtTokenProvider.validateRefreshToken(refreshToken);

		// 사용자 ID 추출
		Long userId = jwtTokenProvider.getUserIdFromToken(refreshToken);

		// Refresh Token 재사용 검증
		refreshTokenService.validateRefreshToken(userId, refreshToken);

		// 사용자 조회 및 계정 상태 확인
		User user = findActiveUserById(userId);

		log.info("토큰 갱신 완료: userId={}", userId);

		//토큰 생성 로직 공통 메서드 사용
		return generateTokenResponse(user);
	}

	/**
	 * 로그아웃
	 */
	@Transactional
	public LogoutResponse logout(Long userId) {
		log.info("로그아웃 시작: userId={}", userId);

		// Redis에서 Refresh Token 삭제
		refreshTokenService.deleteRefreshToken(userId);

		// 더미 액세스 토큰 생성
		String dummyAccessToken = jwtTokenProvider.generateDummyAccessToken();

		log.info("로그아웃 완료: userId={}", userId);

		return LogoutResponse.builder()
			.message("로그아웃이 완료되었습니다.")
			.dummyAccessToken(dummyAccessToken)
			.build();
	}

	/**
	 * 토큰 생성 및 응답 반환
	 */
	private AuthResponse generateTokenResponse(User user) {
		// JWT 토큰 생성
		String accessToken = jwtTokenProvider.generateAccessToken(
			user.getId(),
			user.getRole().getKey()
		);
		String refreshToken = jwtTokenProvider.generateRefreshToken(user.getId());

		// Refresh Token을 Redis에 저장
		refreshTokenService.saveRefreshToken(user.getId(), refreshToken);

		// 응답 반환
		return AuthResponse.of(user, accessToken, refreshToken);
	}

	/**
	 * 이메일로 활성 사용자 조회 (조회 + 상태 확인 통합)
	 */
	private User findActiveUserByEmail(String email) {
		User user = userRepository.findByEmail(email)
			.orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

		if (!user.isActive()) {
			throw new BusinessException(ErrorCode.ACCOUNT_NOT_ACTIVE);
		}

		return user;
	}

	/**
	 * ID로 활성 사용자 조회 (조회 + 상태 확인 통합)
	 */
	private User findActiveUserById(Long userId) {
		User user = userRepository.findByIdAndNotDeleted(userId)
			.orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

		if (!user.isActive()) {
			throw new BusinessException(ErrorCode.ACCOUNT_NOT_ACTIVE);
		}

		return user;
	}
}