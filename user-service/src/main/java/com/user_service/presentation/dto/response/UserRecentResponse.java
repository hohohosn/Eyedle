package com.user_service.presentation.dto.response;

import com.user_service.domain.model.User;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 최근 변경된 사용자 응답 DTO
 * ⭐ Elasticsearch 동기화용 (Search Service에서 사용)
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserRecentResponse {

	private Long userId;
	private String username;
	private String profileImageUrl;  // TODO: 추후 추가
	private String status;
	private LocalDateTime updatedAt;

	/**
	 * User 엔티티를 UserRecentResponse로 변환
	 */
	public static UserRecentResponse from(User user) {
		return UserRecentResponse.builder()
			.userId(user.getId())
			.username(user.getUsername())
			.profileImageUrl(null)  // TODO: 추후 추가
			.status(user.getStatus().name())
			.updatedAt(user.getUpdatedAt())
			.build();
	}
}