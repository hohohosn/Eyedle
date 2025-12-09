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
public class UserResponse {

	private Long id;
	private String email;
	private String username;
	private String slackId;
	private UserRole role;
	private UserStatus status;
	private Boolean deleted;
	private LocalDateTime createdAt;
	private String createdBy;
	private LocalDateTime updatedAt;
	private String updatedBy;

	public static UserResponse from(User user) {
		return UserResponse.builder()
			.id(user.getId())
			.email(user.getEmail())
			.username(user.getUsername())
			.role(user.getRole())
			.status(user.getStatus())
			.deleted(user.deleted())
			.createdAt(user.getCreatedAt())
			.createdBy(user.getCreatedBy())
			.updatedAt(user.getUpdatedAt())
			.updatedBy(user.getUpdatedBy())
			.build();
	}
}
