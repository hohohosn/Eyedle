package com.eyedle.comment_service.infra.client.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FeedGetResultDto {

	private Long feedId;
	private Long userId;
	private String permission;

}
