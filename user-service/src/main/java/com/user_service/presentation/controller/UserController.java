package com.user_service.presentation.controller;

import com.user_service.application.service.UserService;
import com.user_service.presentation.dto.request.UpdateUserRequest;
import com.user_service.presentation.dto.response.ApiResponse;
import com.user_service.presentation.dto.response.UserInfoResponse;
import com.user_service.presentation.dto.response.UserSearchResponse;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

	private final UserService userService;

	/**
	 * 내 정보 조회
	 * GET /api/users/me
	 */
	@GetMapping("/me")
	public ResponseEntity<ApiResponse<UserInfoResponse>> getMyInfo(
		@AuthenticationPrincipal Long userId
	) {
		log.info("내 정보 조회: userId={}", userId);

		UserInfoResponse response = userService.getMyInfo(userId);

		return ResponseEntity.ok(
			ApiResponse.success("사용자 정보를 조회했습니다.", response)
		);
	}

	/**
	 * 내 정보 수정
	 * PATCH /api/users/me
	 */
	@PatchMapping("/me")
	public ResponseEntity<ApiResponse<UserInfoResponse>> updateMyInfo(
		@AuthenticationPrincipal Long userId,
		@Valid @RequestBody UpdateUserRequest request
	) {
		log.info("내 정보 수정: userId={}", userId);

		UserInfoResponse response = userService.updateMyInfo(userId, request);

		return ResponseEntity.ok(
			ApiResponse.success("사용자 정보가 수정되었습니다.", response)
		);
	}

	/**
	 * 회원 탈퇴
	 * DELETE /api/users/me
	 */
	@DeleteMapping("/me")
	public ResponseEntity<ApiResponse<String>> deleteMyAccount(
		@AuthenticationPrincipal Long userId
	) {
		log.info("회원 탈퇴: userId={}", userId);

		userService.deleteMyAccount(userId);

		return ResponseEntity.ok(
			ApiResponse.success("회원 탈퇴가 완료되었습니다.")
		);
	}

	/**
	 * 특정 사용자 정보 조회 (관리자용)
	 * GET /api/users/{userId}
	 */
	@GetMapping("/{userId}")
	public ResponseEntity<ApiResponse<UserInfoResponse>> getUserInfo(
		@PathVariable Long userId
	) {
		log.info("사용자 정보 조회 (관리자): targetUserId={}", userId);

		UserInfoResponse response = userService.getUserInfo(userId);

		return ResponseEntity.ok(
			ApiResponse.success("사용자 정보를 조회했습니다.", response)
		);
	}

	/**
	 * 사용자 검색 (관리자용)
	 * GET /api/users/search?keyword=검색어&page=0&size=20
	 */
	@GetMapping("/search")
	public ResponseEntity<ApiResponse<UserSearchResponse>> searchUsers(
		@RequestParam(required = false) String keyword,
		@PageableDefault(size = 20, sort = "createdAt", direction = Sort.Direction.DESC)
		Pageable pageable
	) {
		log.info("사용자 검색 (관리자): keyword={}, page={}", keyword, pageable.getPageNumber());

		UserSearchResponse response = userService.searchUsers(keyword, pageable);

		return ResponseEntity.ok(
			ApiResponse.success("사용자 검색이 완료되었습니다.", response)
		);
	}
}