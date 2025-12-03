package com.user_service.application.service;

import com.user_service.domain.model.User;
import com.user_service.domain.model.UserStatus;
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

		// 응답 반환
		return AuthResponse;
	}

	// 로그인

	// 토큰 재발급
}
