package com.user_service.application.service;

import com.user_service.infrastructure.exception.BusinessException;
import com.user_service.infrastructure.exception.ErrorCode;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Slf4j
@Service
@RequiredArgsConstructor
public class RefreshTokenService {

	private final RedisTemplate<String, String> redisTemplate;
	private static final String REFRESH_TOKEN_PREFIX = "refresh_token:";
	private static final long REFRESH_TOKEN_EXPIRE_TIME = 7; // 7일

	/**
	 * Refresh Token 저장
	 */
	public void saveRefreshToken(Long userId, String refreshToken) {
		String key = REFRESH_TOKEN_PREFIX + userId;
		redisTemplate.opsForValue().set(key, refreshToken, REFRESH_TOKEN_EXPIRE_TIME, TimeUnit.DAYS);
		log.info("Refresh Token 저장: userId={}", userId);
	}

	/**
	 * Refresh Token 조회
	 */
	public String getRefreshToken(Long userId) {
		String key = REFRESH_TOKEN_PREFIX + userId;
		return redisTemplate.opsForValue().get(key);
	}

	/**
	 * Refresh Token 검증 (재사용 방지)
	 */
	public void validateRefreshToken(Long userId, String refreshToken) {
		String storedToken = getRefreshToken(userId);

		if (storedToken == null) {
			log.error("저장된 Refresh Token이 없음: userId={}", userId);
			throw new BusinessException(ErrorCode.REFRESH_TOKEN_NOT_FOUND);
		}

		if (!storedToken.equals(refreshToken)) {
			log.error("Refresh Token 불일치 (재사용 감지): userId={}", userId);
			// 보안상 기존 토큰도 삭제
			deleteRefreshToken(userId);
			throw new BusinessException(ErrorCode.REFRESH_TOKEN_REUSED);
		}
	}

	/**
	 * Refresh Token 삭제
	 */
	public void deleteRefreshToken(Long userId) {
		String key = REFRESH_TOKEN_PREFIX + userId;
		redisTemplate.delete(key);
		log.info("Refresh Token 삭제: userId={}", userId);
	}
}