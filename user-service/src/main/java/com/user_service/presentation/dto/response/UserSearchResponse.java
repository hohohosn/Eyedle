package com.user_service.presentation.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import org.springframework.data.domain.Page;

import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserSearchResponse {
	private List<UserInfoResponse> users;
	private int currentPage;
	private int totalPages;
	private long totalElements;
	private int size;

	public static UserSearchResponse from(Page<UserInfoResponse> page) {
		return UserSearchResponse.builder()
			.users(page.getContent())
			.currentPage(page.getNumber())
			.totalPages(page.getTotalPages())
			.totalElements(page.getTotalElements())
			.size(page.getSize())
			.build();
	}
}