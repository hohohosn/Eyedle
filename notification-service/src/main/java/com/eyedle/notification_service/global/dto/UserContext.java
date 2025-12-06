package com.eyedle.notification_service.global.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@Builder
@ToString
public class UserContext {
	private Long id;
	private String userId;
	private String role;
}
