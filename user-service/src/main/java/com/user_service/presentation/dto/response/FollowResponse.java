package com.user_service.presentation.dto.response;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FollowResponse {
	private Long followerId;
	private Long followingId;
	private Boolean isFollowing;
	private LocalDateTime createdAt;
}