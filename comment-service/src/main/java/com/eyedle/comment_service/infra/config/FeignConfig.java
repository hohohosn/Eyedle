package com.eyedle.comment_service.infra.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import feign.RequestInterceptor;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Configuration
public class FeignConfig {

	@Bean
	public RequestInterceptor requestInterceptor() {
		return requestTemplate -> {
			ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();

			if (attributes != null) {
				HttpServletRequest request = attributes.getRequest();
				String authorizationHeader = request.getHeader("Authorization");
				String userId = request.getHeader("X-User-Id");
				String role = request.getHeader("X-User-Role");

				if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
					requestTemplate.header("Authorization", authorizationHeader);
					requestTemplate.header("X-User-Id", userId);
					requestTemplate.header("X-User-Role", role);
				}

			}

		};

	}

}
