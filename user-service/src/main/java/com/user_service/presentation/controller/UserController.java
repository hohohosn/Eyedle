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
	 */
	@GetMapping("/me")
	public ResponseEntity<ApiResponse<UserInfoResponse>> getMyInfo(
		@AuthenticationPrincipal Long userId
	) {
		UserInfoResponse response = userService.getMyInfo(userId);

		return ResponseEntity.ok(
			ApiResponse.success("사용자 정보를 조회했습니다.", response)
		);
	}

	/**
	 * 내 정보 수정
	 */
	@PatchMapping("/me")
	public ResponseEntity<ApiResponse<UserInfoResponse>> updateMyInfo(
		@AuthenticationPrincipal Long userId,
		@Valid @RequestBody UpdateUserRequest request
	) {
		UserInfoResponse response = userService.updateMyInfo(userId, request);

		return ResponseEntity.ok(
			ApiResponse.success("사용자 정보가 수정되었습니다.", response)
		);
	}

	/**
	 * 회원 탈퇴
	 */
	@DeleteMapping("/me")
	public ResponseEntity<ApiResponse<String>> deleteMyAccount(
		@AuthenticationPrincipal Long userId
	) {
		userService.deleteMyAccount(userId);

		return ResponseEntity.ok(
			ApiResponse.success("회원 탈퇴가 완료되었습니다.")
		);
	}

	/**
	 * 특정 사용자 조회 (활성 회원)
	 */
	@GetMapping("/{userId}")
	public ResponseEntity<ApiResponse<UserInfoResponse>> getUserInfo(
		@PathVariable Long userId
	) {
		UserInfoResponse response = userService.getUserInfo(userId);

		return ResponseEntity.ok(
			ApiResponse.success("사용자 정보를 조회했습니다.", response)
		);
	}

	/**
	 * 특정 사용자 조회 (탈퇴 회원 포함)
	 */
	@GetMapping("/{userId}/with-deleted")
	public ResponseEntity<ApiResponse<UserInfoResponse>> getUserInfoWithDeleted(
		@PathVariable Long userId
	) {
		UserInfoResponse response = userService.getUserInfoIncludingDeleted(userId);

		return ResponseEntity.ok(
			ApiResponse.success("사용자 정보를 조회했습니다.", response)
		);
	}

	/**
	 * 사용자 검색 (관리용)
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