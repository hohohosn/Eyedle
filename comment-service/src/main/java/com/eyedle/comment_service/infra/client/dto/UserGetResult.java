package com.eyedle.comment_service.infra.client.dto;

import com.eyedle.comment_service.domain.vo.Author;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserGetResult {
	private Long id;
	private String userId;
	private String profileImageUrl;

	public Author toAuthor() {
		return Author.builder()
			.id(this.id)
			.name(this.userId)
			.profileImgUrl(this.profileImageUrl)
			.build();
	}
}
