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
	private Long id;

	@Column(name = "author_name", nullable = false)
	private String name;

	@Column(name = "author_profile_img")
	private String profileImg;

	@Builder
	public Author(Long id, String name, String profileImgUrl) {
		this.id = id;
		this.name = name;
		this.profileImg = profileImgUrl;
	}
}
