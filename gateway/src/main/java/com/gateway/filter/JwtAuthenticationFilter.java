package com.gateway.filter;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.server.ServerWebExchange;

import reactor.core.publisher.Mono;

import javax.crypto.SecretKey;

import java.nio.charset.StandardCharsets;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter implements GatewayFilter {

	@Value("${jwt.secret}")
	private String jwtSecret;

	@Override
	public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
		ServerHttpRequest request = exchange.getRequest();

		// Authorization 헤더 추출
		String authorizationHeader = request.getHeaders().getFirst("Authorization");

		// Bearer 토큰 검증
		if (!StringUtils.hasText(authorizationHeader) || !authorizationHeader.startsWith("Bearer ")) {
			log.warn("Authorization 헤더가 없거나 형식이 잘못됨: {}", authorizationHeader);
			exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
			return exchange.getResponse().setComplete();
		}

		// 토큰 추출
		String token = authorizationHeader.substring(7);

		try {
			//검증 및 파싱
			SecretKey key = Keys.hmacShaKeyFor(jwtSecret.getBytes(StandardCharsets.UTF_8));

			Claims claims = Jwts.parserBuilder()
				.setSigningKey(key)
				.build()
				.parseClaimsJws(token)
				.getBody();

			//사용자 정보 추출
			String userId = claims.getSubject();
			String email = claims.get("email", String.class);
			String role = claims.get("role", String.class);

			log.info("JWT 인증 성공: userId={}, email={}, role={}", userId, email, role);

			ServerHttpRequest mutatedRequest = request.mutate()
				.header("X-User-Id", userId)
				.header("X-User-Email", email)
				.header("X-User-Role", role)
				.build();

			return chain.filter(exchange.mutate().request(mutatedRequest).build());

		} catch (Exception e) {
			log.error("JWT 토큰 검증 실패: {}", e.getMessage());
			exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
			return exchange.getResponse().setComplete();
		}
	}
}