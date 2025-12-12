package com.search_service.infra.client.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FeedClientResponse {

	private Long feedId;

	private Long userId;

	private String content;

	private String permission;

	private Integer likeCount;

	private LocalDateTime createdAt;

	private LocalDateTime updatedAt;

	private Boolean isDeleted;

	private List<FeedMediaDto> medias;
	private List<String> tags;

	@Getter
	@NoArgsConstructor
	@AllArgsConstructor
	public static class FeedMediaDto {
		private Long mediaId;
		private String mediaType;
		private String mediaUrl;
		private String thumbnailUrl;
	}
}
