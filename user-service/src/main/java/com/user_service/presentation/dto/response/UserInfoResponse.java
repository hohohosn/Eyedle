package com.user_service.presentation.dto.response;

import com.user_service.domain.model.User;
import com.user_service.domain.model.UserRole;
import com.user_service.domain.model.UserStatus;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserInfoResponse {
	private Long id;
	private String email;
	private String username;
	private UserRole role;
	private UserStatus status;
	private LocalDateTime createdAt;

	public static UserInfoResponse from(User user) {
		return UserInfoResponse.builder()
			.id(user.getId())
			.email(user.getEmail())
			.username(user.getUsername())
			.role(user.getRole())
			.status(user.getStatus())
			.createdAt(user.getCreatedAt())
			.build();
	}
}