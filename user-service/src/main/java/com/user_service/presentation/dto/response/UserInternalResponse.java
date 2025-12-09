package com.user_service.presentation.dto.response;

import com.user_service.domain.model.User;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * Internal API용 유저 정보 응답
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserInternalResponse {
	private Long id;
	private String username;
	private String email;
	private String profileImageUrl; // 추후 프로필 이미지 추가 시 사용

	public static UserInternalResponse from(User user) {
		return UserInternalResponse.builder()
			.id(user.getId())
			.username(user.getUsername())
			.email(user.getEmail())
			.profileImageUrl(null) // TODO: 프로필 이미지 필드 추가 후 매핑
			.build();
	}
}