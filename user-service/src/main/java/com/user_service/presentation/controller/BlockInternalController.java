package com.user_service.presentation.controller;

import com.user_service.application.service.BlockService;
import com.user_service.presentation.dto.response.ApiResponse;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/internal/blocks")
@RequiredArgsConstructor
public class BlockInternalController {

	private final BlockService blockService;

	/**
	 * 차단 여부 확인
	 */
	@GetMapping("/check")
	public boolean checkBlock(
		@RequestParam Long blockerId,
		@RequestParam Long blockedId
	) {
		return blockService.isBlocked(blockerId, blockedId);
	}

	/**
	 * 양측 차단 여부 확인(한쪽이라도 차단한 경우 true 반환)
	 */
	@GetMapping("/check-either")
	public boolean checkEitherBlocked(
		@RequestParam Long userId1,
		@RequestParam Long userId2
	) {
		return blockService.isEitherBlocked(userId1, userId2);
	}

	/**
	 * 서로 차단 아닐 때만 true, 한 쪽이라도 차단한 경우 false(Feign client)
	 */
	@GetMapping("/is-blocked")
	public boolean isNotBlocked(
		@RequestParam Long userId1,
		@RequestParam Long userId2
	) {
		// 양방향 차단 확인 후 반대값 반환 (차단 아니면 true)
		return !blockService.isEitherBlocked(userId1, userId2);
	}

	/**
	 * 차단 여부 일괄 조회
	 */
	@PostMapping("/check-batch")
	public Map<Long, Boolean> checkBlockedBatch(
		@RequestParam Long blockerId,
		@RequestBody List<Long> targetUserIds
	) {
		return blockService.checkBlockedBatch(blockerId, targetUserIds);
	}
}