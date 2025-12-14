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
@RequestMapping("/api/internal/blocks")
@RequiredArgsConstructor
public class BlockInternalController {

	private final BlockService blockService;

	/**
	 * 차단 여부 확인
	 */
	@GetMapping("/check")
	public ResponseEntity<ApiResponse<Boolean>> checkBlock(
		@RequestParam Long blockerId,
		@RequestParam Long blockedId
	) {

		boolean isBlocked = blockService.isBlocked(blockerId, blockedId);

		return ResponseEntity.ok(
			ApiResponse.success("차단 여부를 조회했습니다.", isBlocked)
		);
	}

	/**
	 * 양측 차단 여부 확인(한쪽이라도 차단한 경우 true 반환)
	 */
	@GetMapping("/check-either")
	public ResponseEntity<ApiResponse<Boolean>> checkEitherBlocked(
		@RequestParam Long userId1,
		@RequestParam Long userId2
	) {

		boolean isBlocked = blockService.isEitherBlocked(userId1, userId2);

		return ResponseEntity.ok(
			ApiResponse.success("양측 차단 여부를 조회했습니다.", isBlocked)
		);
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
		boolean isBlocked = blockService.isEitherBlocked(userId1, userId2);
		return !isBlocked;
	}

	/**
	 * 차단 여부 일괄 조회
	 */
	@PostMapping("/check-batch")
	public ResponseEntity<ApiResponse<Map<Long, Boolean>>> checkBlockedBatch(
		@RequestParam Long blockerId,
		@RequestBody List<Long> targetUserIds
	) {

		Map<Long, Boolean> result = blockService.checkBlockedBatch(blockerId, targetUserIds);

		return ResponseEntity.ok(
			ApiResponse.success("차단 여부를 일괄 조회했습니다.", result)
		);
	}
}