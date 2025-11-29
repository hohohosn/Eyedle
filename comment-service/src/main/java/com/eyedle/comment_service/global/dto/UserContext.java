package com.eyedle.comment_service.global.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@Builder
@ToString
public class UserContext {
	private Long userId;
	private String userName;
	private String role;
}
