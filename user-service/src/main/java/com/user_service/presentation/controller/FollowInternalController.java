package com.user_service.presentation.controller;

import com.user_service.application.service.FollowService;
import com.user_service.presentation.dto.request.FollowCheckBatchRequest;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/internal/follows")
@RequiredArgsConstructor
public class FollowInternalController {

	private final FollowService followService;

	/**
	 * 팔로우 여부 확인
	 */
	@GetMapping("/is-following")
	public boolean isFollowing(
		@RequestParam("fromUserId") Long fromUserId,
		@RequestParam("toUserId") Long toUserId
	) {
		log.info("내부 API - 팔로우 여부 확인: from={}, to={}", fromUserId, toUserId);
		return followService.isFollowing(fromUserId, toUserId);
	}

	/**
	 * 팔로우 여부 일괄 조회
	 */
	@PostMapping("/check-batch")
	public Map<Long, Boolean> checkFollowingBatch(
		@RequestBody FollowCheckBatchRequest request
	) {

		return followService.checkFollowingBatch(
			request.getFollowerId(),
			request.getFollowingIds()
		);
	}
}
