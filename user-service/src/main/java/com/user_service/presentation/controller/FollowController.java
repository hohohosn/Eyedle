package com.user_service.presentation.controller;

import com.user_service.application.service.FollowService;
import com.user_service.presentation.dto.response.ApiResponse;
import com.user_service.presentation.dto.response.FollowResponse;
import com.user_service.presentation.dto.response.UserInternalResponse;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/follows")
@RequiredArgsConstructor
public class FollowController {

	private final FollowService followService;

	/**
	 * 팔로우
	 */
	@PostMapping("/{userId}")
	public ResponseEntity<ApiResponse<FollowResponse>> follow(
		@AuthenticationPrincipal Long currentUserId,
		@PathVariable Long userId
	) {

		FollowResponse response = followService.follow(currentUserId, userId);

		return ResponseEntity
			.status(HttpStatus.CREATED)
			.body(ApiResponse.success("팔로우했습니다.", response));
	}

	/**
	 * 언팔로우
	 */
	@DeleteMapping("/{userId}")
	public ResponseEntity<ApiResponse<Void>> unfollow(
		@AuthenticationPrincipal Long currentUserId,
		@PathVariable Long userId
	) {

		followService.unfollow(currentUserId, userId);

		return ResponseEntity.ok(
			ApiResponse.success("언팔로우했습니다.", null)
		);
	}

	/**
	 * 팔로워 목록 조회 (나를 팔로우하는 사람들)
	 */
	@GetMapping("/followers")
	public ResponseEntity<ApiResponse<Slice<UserInternalResponse>>> getFollowers(
		@AuthenticationPrincipal Long currentUserId,
		@PageableDefault(size = 20, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable
	) {

		Slice<UserInternalResponse> followers = followService.getFollowers(currentUserId, pageable);

		return ResponseEntity.ok(
			ApiResponse.success("팔로워 목록을 조회했습니다.", followers)
		);
	}

	/**
	 * 팔로잉 목록 조회 (내가 팔로우하는 사람들)
	 */
	@GetMapping("/followings")
	public ResponseEntity<ApiResponse<Slice<UserInternalResponse>>> getFollowings(
		@AuthenticationPrincipal Long currentUserId,
		@PageableDefault(size = 20, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable
	) {

		Slice<UserInternalResponse> followings = followService.getFollowings(currentUserId, pageable);

		return ResponseEntity.ok(
			ApiResponse.success("팔로잉 목록을 조회했습니다.", followings)
		);
	}

	/**
	 * 특정 유저의 팔로워 목록 조회
	 */
	@GetMapping("/{userId}/followers")
	public ResponseEntity<ApiResponse<Slice<UserInternalResponse>>> getUserFollowers(
		@PathVariable Long userId,
		@PageableDefault(size = 20, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable
	) {

		Slice<UserInternalResponse> followers = followService.getFollowers(userId, pageable);

		return ResponseEntity.ok(
			ApiResponse.success("팔로워 목록을 조회했습니다.", followers)
		);
	}

	/**
	 * 특정 유저의 팔로잉 목록 조회
	 */
	@GetMapping("/{userId}/followings")
	public ResponseEntity<ApiResponse<Slice<UserInternalResponse>>> getUserFollowings(
		@PathVariable Long userId,
		@PageableDefault(size = 20, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable
	) {

		Slice<UserInternalResponse> followings = followService.getFollowings(userId, pageable);

		return ResponseEntity.ok(
			ApiResponse.success("팔로잉 목록을 조회했습니다.", followings)
		);
	}

	/**
	 * 팔로우 여부 확인
	 */
	@GetMapping("/{userId}/check")
	public ResponseEntity<ApiResponse<Boolean>> checkFollowing(
		@AuthenticationPrincipal Long currentUserId,
		@PathVariable Long userId
	) {
		boolean isFollowing = followService.isFollowing(currentUserId, userId);

		return ResponseEntity.ok(
			ApiResponse.success("팔로우 여부를 확인했습니다.", isFollowing)
		);
	}
}