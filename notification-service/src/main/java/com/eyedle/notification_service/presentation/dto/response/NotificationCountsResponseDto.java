package com.eyedle.notification_service.presentation.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NotificationCountsResponseDto {
	private Long count;

	public static NotificationCountsResponseDto toDto(Long count) {
		return NotificationCountsResponseDto.builder()
				.count(count)
				.build();
	}
}
