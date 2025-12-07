package com.user_service.presentation.dto.response;

import com.user_service.domain.model.User;
import com.user_service.domain.model.UserRole;
import com.user_service.domain.model.UserStatus;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AuthResponse {

	private String accessToken;
	private String refreshToken;
	private Long userId;
	private String email;
	private String username;
	private UserRole role;
	private UserStatus status;
	private LocalDateTime createdAt;

	public static AuthResponse of(User user, String accessToken, String refreshToken) {
		return AuthResponse.builder()
			.accessToken(accessToken)
			.refreshToken(refreshToken)
			.userId(user.getId())
			.email(user.getEmail())
			.username(user.getUsername())
			.role(user.getRole())
			.status(user.getStatus())
			.createdAt(user.getCreatedAt())
			.build();
	}
}