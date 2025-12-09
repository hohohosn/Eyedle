package com.user_service.presentation.dto.response;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FollowListResponse {
	private List<UserInternalResponse> users;
	private Integer currentPage;
	private Integer size;
	private Boolean hasNext;
}