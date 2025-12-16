package com.user_service.presentation.controller;

import com.user_service.application.service.BlockService;
import com.user_service.presentation.dto.response.ApiResponse;
import com.user_service.presentation.dto.response.BlockResponse;
import com.user_service.presentation.dto.response.UserInternalResponse;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/blocks")
@RequiredArgsConstructor
public class BlockController {

	private final BlockService blockService;

	/**
	 * 사용자 차단
	 */
	@PostMapping("/{userId}")
	public ResponseEntity<ApiResponse<BlockResponse>> blockUser(
		@AuthenticationPrincipal Long currentUserId,
		@PathVariable Long userId
	) {

		BlockResponse response = blockService.blockUser(currentUserId, userId);

		return ResponseEntity.ok(
			ApiResponse.success("사용자를 차단했습니다.", response)
		);
	}

	/**
	 * 차단 해제
	 */
	@DeleteMapping("/{userId}")
	public ResponseEntity<ApiResponse<Void>> unblockUser(
		@AuthenticationPrincipal Long currentUserId,
		@PathVariable Long userId
	) {

		blockService.unblockUser(currentUserId, userId);

		return ResponseEntity.ok(
			ApiResponse.success("차단을 해제했습니다.")
		);
	}

	/**
	 * 차단 여부 확인
	 */
	@GetMapping("/check/{userId}")
	public ResponseEntity<ApiResponse<Boolean>> checkBlocked(
		@AuthenticationPrincipal Long currentUserId,
		@PathVariable Long userId
	) {
		boolean isBlocked = blockService.isBlocked(currentUserId, userId);

		return ResponseEntity.ok(
			ApiResponse.success("차단 여부를 조회했습니다.", isBlocked)
		);
	}

	/**
	 * 내가 차단한 사용자 목록 조회
	 */
	@GetMapping
	public ResponseEntity<ApiResponse<Slice<UserInternalResponse>>> getBlockedUsers(
		@AuthenticationPrincipal Long currentUserId,
		@PageableDefault(size = 20, sort = "createdAt", direction = Sort.Direction.DESC)
		Pageable pageable
	) {
		Slice<UserInternalResponse> response = blockService.getBlockedUsers(
			currentUserId, pageable
		);

		return ResponseEntity.ok(
			ApiResponse.success("차단한 사용자 목록을 조회했습니다.", response)
		);
	}
}