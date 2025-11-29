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

	private static final Long DEFAULT_USER_ID = 1L;

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
		FilterChain filterChain) throws ServletException, IOException {

		String userIdStr = request.getHeader("X-User-Id");
		String roleStr = request.getHeader("X-User-Role");
		String userNameStr = request.getHeader("X-User-Name");

		Long userId;
		String role;
		String userName;

		if (userIdStr != null) {
			try {
				userId = Long.parseLong(userIdStr);
				role = (roleStr != null) ? roleStr : "USER";
				userName = (userNameStr != null) ? userNameStr : "tester";
			} catch (NumberFormatException e) {
				// 헤더가 이상하면 기본값으로 폴백하거나 무시
				userId = DEFAULT_USER_ID;
				role = "USER";
				userName = "tester";
			}
		} else {
			userId = DEFAULT_USER_ID;
			role = "USER";
			userName = "default-tester";
		}

		UserContext userContext = UserContext.builder()
			.userId(userId)
			.userName(userName)
			.role(role)
			.build();

		UsernamePasswordAuthenticationToken authentication =
			new UsernamePasswordAuthenticationToken(userContext, null, Collections.emptyList());

		SecurityContextHolder.getContext().setAuthentication(authentication);

		filterChain.doFilter(request, response);
	}
}
