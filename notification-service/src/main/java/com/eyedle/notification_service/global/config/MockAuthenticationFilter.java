package com.eyedle.notification_service.global.config;

import java.io.IOException;
import java.util.Collections;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import com.eyedle.comment_service.global.dto.UserContext;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class MockAuthenticationFilter extends OncePerRequestFilter {

	private static final Long DEFAULT_ID = 1L;

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
		FilterChain filterChain) throws ServletException, IOException {

		log.info("=======================================================");
		log.info("🔥 [MockFilter] 요청 감지! URL: {}", request.getRequestURI());

		String userIdStr = request.getHeader("X-User-Id");
		log.info("🔥 [MockFilter] 헤더 값 X-User-Id: {}", userIdStr);
		String roleStr = request.getHeader("X-User-Role");
		String userNameStr = request.getHeader("X-User-Name");


		Long id;
		String role;
		String userId;

		if (userIdStr != null) {
			try {
				id = Long.parseLong(userIdStr);
				role = (roleStr != null) ? roleStr : "USER";
				userId = (userNameStr != null) ? userNameStr : "tester";
			} catch (NumberFormatException e) {
				// 헤더가 이상하면 기본값으로 폴백하거나 무시
				id = DEFAULT_ID;
				role = "USER";
				userId = "tester";
				log.info("⚠️ [MockFilter] 헤더 없음 -> 기본 테스트 계정({})으로 로그인 처리", userId);
			}
		} else {
			id = DEFAULT_ID;
			role = "USER";
			userId = "default-tester";
			log.info("⚠️ [MockFilter] 헤더 없음 -> 기본 테스트 계정({})으로 로그인 처리", userId);
		}

		UserContext userContext = UserContext.builder()
			.id(id)
			.userId(userId)
			.role(role)
			.build();

		UsernamePasswordAuthenticationToken authentication =
			new UsernamePasswordAuthenticationToken(userContext, null, Collections.emptyList());

		SecurityContextHolder.getContext().setAuthentication(authentication);

		log.info("✅ [MockFilter] 인증 완료! SecurityContext에 저장된 유저: {}", userId);
		log.info("=======================================================");

		filterChain.doFilter(request, response);
	}
}
