package com.user_service.presentation.dto.request;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class FollowCheckBatchRequest {
	private Long followerId;
	private List<Long> followingIds;
}