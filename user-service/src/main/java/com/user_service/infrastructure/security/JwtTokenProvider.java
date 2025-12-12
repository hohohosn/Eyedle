package com.user_service.infrastructure.security;

import com.user_service.infrastructure.exception.BusinessException;
import com.user_service.infrastructure.exception.ErrorCode;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;

import java.nio.charset.StandardCharsets;
import java.util.Date;

@Slf4j
@Component
@Getter
public class JwtTokenProvider {

	private final SecretKey secretKey;
	private final long accessTokenExpiration;
	private final long refreshTokenExpiration;

	public JwtTokenProvider(
		@Value("${jwt.secret}") String secret,
		@Value("${jwt.access-token-expiration}") long accessTokenExpiration,
		@Value("${jwt.refresh-token-expiration}") long refreshTokenExpiration
	) {
		this.secretKey = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
		this.accessTokenExpiration = accessTokenExpiration;
		this.refreshTokenExpiration = refreshTokenExpiration;
	}

	/**
	 * Access Token 생성
	 */
	public String generateAccessToken(Long userId, String role) {
		Date now = new Date();
		Date expiryDate = new Date(now.getTime() + accessTokenExpiration);

		return Jwts.builder()
			.setSubject(String.valueOf(userId))
			.claim("role", role)
			.claim("type", "access")
			.setIssuedAt(now)
			.setExpiration(expiryDate)
			.signWith(secretKey, SignatureAlgorithm.HS256)
			.compact();
	}

	/**
	 * Refresh Token 생성
	 */
	public String generateRefreshToken(Long userId) {
		Date now = new Date();
		Date expiryDate = new Date(now.getTime() + refreshTokenExpiration);

		return Jwts.builder()
			.setSubject(String.valueOf(userId))
			.claim("type", "refresh")
			.setIssuedAt(now)
			.setExpiration(expiryDate)
			.signWith(secretKey, SignatureAlgorithm.HS256)
			.compact();
	}

	/**
	 * 더미 액세스 토큰 생성 (로그아웃용)
	 */
	public String generateDummyAccessToken() {
		Date now = new Date();
		Date expiryDate = new Date(now.getTime() - 1000); // 이미 만료된 시간

		return Jwts.builder()
			.setSubject("0")  // 존재하지 않는 사용자 ID
			.claim("role", "NONE")
			.claim("type", "dummy")
			.setIssuedAt(now)
			.setExpiration(expiryDate)
			.signWith(secretKey, SignatureAlgorithm.HS256)
			.compact();
	}

	/**
	 * 토큰에서 사용자 ID 추출
	 */
	public Long getUserIdFromToken(String token) {
		Claims claims = parseClaims(token);
		return Long.parseLong(claims.getSubject());
	}

	/**
	 * 토큰에서 역할 추출
	 */
	public String getRoleFromToken(String token) {
		Claims claims = parseClaims(token);
		return claims.get("role", String.class);
	}

	/**
	 * 토큰에서 타입 추출
	 */
	public String getTokenType(String token) {
		Claims claims = parseClaims(token);
		return claims.get("type", String.class);
	}

	/**
	 * 토큰 검증 (유효한 토큰인지)
	 */
	public boolean validateToken(String token) {
		try {
			Jwts.parserBuilder()
				.setSigningKey(secretKey)
				.build()
				.parseClaimsJws(token);
			return true;
		} catch (io.jsonwebtoken.security.SecurityException | MalformedJwtException e) {
			log.error("잘못된 JWT 서명입니다.", e);
		} catch (ExpiredJwtException e) {
			log.error("만료된 JWT 토큰입니다.", e);
		} catch (UnsupportedJwtException e) {
			log.error("지원되지 않는 JWT 토큰입니다.", e);
		} catch (IllegalArgumentException e) {
			log.error("JWT 토큰이 잘못되었습니다.", e);
		}
		return false;
	}

	/**
	 * Access Token이 만료되었는지 확인
	 */
	public boolean isTokenExpired(String token) {
		try {
			Jwts.parserBuilder()
				.setSigningKey(secretKey)
				.build()
				.parseClaimsJws(token);
			return false;
		} catch (ExpiredJwtException e) {
			return true;
		} catch (Exception e) {
			throw new BusinessException(ErrorCode.INVALID_TOKEN);
		}
	}

	/**
	 * 토큰이 Access Token인지 확인
	 */
	public boolean isAccessToken(String token) {
		String type = getTokenType(token);
		return "access".equals(type);
	}

	/**
	 * 토큰이 Refresh Token인지 확인
	 */
	public boolean isRefreshToken(String token) {
		String type = getTokenType(token);
		return "refresh".equals(type);
	}

	/**
	 * Refresh Token 통합 검증
	 */
	public void validateRefreshToken(String token) {
		//유효성 검증
		if (!validateToken(token)) {
			throw new BusinessException(ErrorCode.INVALID_TOKEN);
		}

		//Refresh Token 타입 검증
		if (!isRefreshToken(token)) {
			throw new BusinessException(ErrorCode.INVALID_TOKEN);
		}
	}

	/**
	 * 토큰에서 Claims 파싱 (만료된 토큰도 파싱 가능)
	 */
	private Claims parseClaims(String token) {
		try {
			return Jwts.parserBuilder()
				.setSigningKey(secretKey)
				.build()
				.parseClaimsJws(token)
				.getBody();
		} catch (ExpiredJwtException e) {
			return e.getClaims();
		} catch (Exception e) {
			throw new BusinessException(ErrorCode.INVALID_TOKEN);
		}
	}
}