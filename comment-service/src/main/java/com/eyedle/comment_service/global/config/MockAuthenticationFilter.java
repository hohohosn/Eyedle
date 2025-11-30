package com.eyedle.comment_service.global.config;

import java.io.IOException;
import java.util.Collections;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import com.eyedle.comment_service.global.dto.UserContext;
import com.github.f4b6a3.tsid.TsidCreator;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class MockAuthenticationFilter extends OncePerRequestFilter {

	private static final Long DEFAULT_ID = 1L;

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
		FilterChain filterChain) throws ServletException, IOException {

		String userIdStr = request.getHeader("X-User-Id");
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
			}
		} else {
			id = DEFAULT_ID;
			role = "USER";
			userId = "default-tester";
		}

		UserContext userContext = UserContext.builder()
			.id(id)
			.userId(userId)
			.role(role)
			.build();

		UsernamePasswordAuthenticationToken authentication =
			new UsernamePasswordAuthenticationToken(userContext, null, Collections.emptyList());

		SecurityContextHolder.getContext().setAuthentication(authentication);

		filterChain.doFilter(request, response);
	}
}
