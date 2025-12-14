package com.user_service.presentation.dto.response;

import lombok.*;

import java.time.LocalDateTime;

/**
 * 차단 응답 DTO
 */
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class BlockResponse {

	private Long blockerId;
	private Long blockedId;
	private boolean isBlocked;
	private LocalDateTime createdAt;
}