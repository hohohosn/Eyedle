package com.eyedle.comment_service.domain.vo;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Embeddable
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Author {

	@Column(name = "author_id", nullable = false)
	private Long authorId;

	@Column(name = "author_name", nullable = false)
	private String authorName;

	@Column(name = "author_profile_img")
	private String authorProfileImg;

	@Builder
	public Author(Long authorId, String authorName, String profileImgUrl) {
		this.authorId = authorId;
		this.authorName = authorName;
		this.authorProfileImg = profileImgUrl;
	}
}
