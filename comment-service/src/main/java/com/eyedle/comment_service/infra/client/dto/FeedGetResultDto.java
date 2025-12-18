package com.eyedle.comment_service.infra.client.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FeedGetResultDto {

	@JsonProperty("id")
	private Long feedId;

	@JsonProperty("user")
	private FeedUserDtoInner user;

	private String permission;

	public Long getUserId() {
		return user != null ? user.getId() : null;
	}

	@Getter
	@NoArgsConstructor
	@JsonIgnoreProperties(ignoreUnknown = true)
	public static class FeedUserDtoInner {
		private Long id;
		private String username;
		private String email;
	}

}
