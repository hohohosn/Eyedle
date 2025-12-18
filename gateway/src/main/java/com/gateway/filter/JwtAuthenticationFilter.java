package com.gateway.filter;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
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
public class JwtAuthenticationFilter extends AbstractGatewayFilterFactory<JwtAuthenticationFilter.Config> {

	@Value("${jwt.secret}")
	private String jwtSecret;

	public JwtAuthenticationFilter() {
		super(Config.class);
	}

	@Override
	public GatewayFilter apply(Config config) {
		return (exchange, chain) -> {
			ServerHttpRequest request = exchange.getRequest();

			// Authorization 헤더 추출
			String authorizationHeader = request.getHeaders().getFirst("Authorization");

			// Bearer 토큰 검증
			if (!StringUtils.hasText(authorizationHeader) || !authorizationHeader.startsWith("Bearer ")) {
				log.warn("Authorization 헤더가 없거나 형식이 잘못됨: {}", authorizationHeader);
				return onError(exchange, HttpStatus.UNAUTHORIZED);
			}

			// 토큰 추출
			String token = authorizationHeader.substring(7).trim();

			try {
				// 검증 및 파싱
				System.out.println("check point 1");
				SecretKey key = Keys.hmacShaKeyFor(jwtSecret.getBytes(StandardCharsets.UTF_8));

				System.out.println("check point 2");
				Claims claims = Jwts.parserBuilder()
					.setSigningKey(key)
					.build()
					.parseClaimsJws(token)
					.getBody();

				// 사용자 정보 추출
				System.out.println("check point 3");
				String userId = claims.getSubject();
				String role = claims.get("role", String.class);

				log.info("JWT 인증 성공: userId={}, role={}", userId, role);

				// 요청 헤더에 사용자 정보 추가
				ServerHttpRequest mutatedRequest = request.mutate()
					.header("X-User-Id", userId)
					.header("X-User-Role", role)
					.build();

				return chain.filter(exchange.mutate().request(mutatedRequest).build());

			} catch (Exception e) {
				log.error("JWT 토큰 검증 실패: {}", e.getMessage());
				return onError(exchange, HttpStatus.UNAUTHORIZED);
			}
		};
	}

	private Mono<Void> onError(ServerWebExchange exchange, HttpStatus status) {
		exchange.getResponse().setStatusCode(status);
		return exchange.getResponse().setComplete();
	}

	public static class Config {
		// 나중에 필요하면 여기에 설정 추가
	}
}