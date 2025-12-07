package com.user_service.infrastructure.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Optional;

@Configuration
@EnableJpaAuditing(auditorAwareRef = "auditorProvider")
public class JpaConfig {

	@Bean
	public AuditorAware<String> auditorProvider() {
		return () -> {
			Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

			if (authentication == null || !authentication.isAuthenticated() ||
				"anonymousUser".equals(authentication.getPrincipal())) {
				return Optional.of("SYSTEM");
			}

			// JWT에서 추출한 userId 사용
			return Optional.of(String.valueOf(authentication.getPrincipal()));
		};
	}
}