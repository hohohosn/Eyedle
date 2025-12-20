package com.user_service.infrastructure.config;

import com.user_service.infrastructure.security.JwtAuthenticationFilter;
import com.user_service.infrastructure.security.JwtTokenProvider;

import lombok.RequiredArgsConstructor;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

	private final JwtTokenProvider jwtTokenProvider;

	@Bean
	public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
		http
			// CSRF 비활성화
			.csrf(AbstractHttpConfigurer::disable)

			// 세션 사용 안 함
			.sessionManagement(session ->
				session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
			)

			// 권한 설정
			.authorizeHttpRequests(auth -> auth
				// 인증 불필요
				.requestMatchers("/auth/**").permitAll()
				.requestMatchers("/actuator/**").permitAll()
				.requestMatchers("/health").permitAll()
				.requestMatchers("/chat", "/chat/**").permitAll()  // websocket 요청을 허용

				// Internal API
				.requestMatchers("/internal/**").permitAll()

				// 관리자 전용
				.requestMatchers("/users/search").hasRole("ADMIN")

				//인증 필요
				.requestMatchers(HttpMethod.GET, "/users/{userId}").authenticated()

				// 나머지는 인증 필요
				.anyRequest().authenticated()
			)

			// JWT 필터 추가
			.addFilterBefore(
				new JwtAuthenticationFilter(jwtTokenProvider),
				UsernamePasswordAuthenticationFilter.class
			);

		return http.build();
	}

	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}
}