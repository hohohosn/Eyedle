package com.user_service.infrastructure.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

	private final JwtTokenProvider jwtTokenProvider;

	@Override
	protected void doFilterInternal(
		HttpServletRequest request,
		HttpServletResponse response,
		FilterChain filterChain
	) throws ServletException, IOException {

		try {
			// 토큰 추출
			String token = extractTokenFromRequest(request);

			// 토큰 유효성 검증
			if (token != null && jwtTokenProvider.validateToken(token)) {
				// 사용자 정보 추출
				Long userId = jwtTokenProvider.getUserIdFromToken(token);
				String role = jwtTokenProvider.getRoleFromToken(token);

				// Authentication 객체 생성
				List<SimpleGrantedAuthority> authorities = List.of(
					new SimpleGrantedAuthority(role)
				);

				UsernamePasswordAuthenticationToken authentication =
					new UsernamePasswordAuthenticationToken(
						userId,
						null,
						authorities
					);

				authentication.setDetails(
					new WebAuthenticationDetailsSource().buildDetails(request)
				);

				// SecurityContext에 인증 정보 설정
				SecurityContextHolder.getContext().setAuthentication(authentication);

				log.debug("JWT 인증 성공: userId={}, role={}", userId, role);
			}
		} catch (Exception e) {
			log.error("JWT 인증 실패", e);
		}

		filterChain.doFilter(request, response);
	}

	/**
	 * Request에서 JWT 토큰 추출
	 */
	private String extractTokenFromRequest(HttpServletRequest request) {
		String bearerToken = request.getHeader("Authorization");

		if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
			return bearerToken.substring(7);
		}

		return null;
	}
}