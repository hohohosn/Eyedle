package com.user_service.application.service;

import com.user_service.infrastructure.exception.BusinessException;
import com.user_service.infrastructure.exception.ErrorCode;
import com.user_service.infrastructure.security.JwtTokenProvider;

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
	private final JwtTokenProvider jwtTokenProvider;

	private static final String REFRESH_TOKEN_PREFIX = "refresh_token:";

	/**
	 * Refresh Token 저장(토큰 만료 시간(7일) 일치)
	 */
	public void saveRefreshToken(Long userId, String refreshToken) {
		String key = REFRESH_TOKEN_PREFIX + userId;

		long expirationMs = jwtTokenProvider.getRefreshTokenExpiration();

		redisTemplate.opsForValue().set(
			key,
			refreshToken,
			expirationMs,
			TimeUnit.MILLISECONDS
		);

		log.info("Refresh Token 저장: userId={}, TTL={}ms", userId, expirationMs);
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

	/**
	 * 남은 TTL 조회 (디버깅용)
	 */
	public Long getTimeToLive(Long userId) {
		String key = REFRESH_TOKEN_PREFIX + userId;
		return redisTemplate.getExpire(key, TimeUnit.SECONDS);
	}
}