package com.user_service.domain.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum UserStatus {
	ACTIVE("활성", "정상적으로 사용 가능한 계정"),
	INACTIVE("비활성", "일시적으로 사용 중지된 계정"),
	SUSPENDED("정지", "관리자에 의해 정지된 계정"),
	DELETED("삭제", "삭제된 계정");

	private final String title;
	private final String description;
}