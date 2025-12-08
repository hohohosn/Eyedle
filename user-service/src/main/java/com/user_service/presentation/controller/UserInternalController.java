package com.user_service.presentation.controller;

import com.user_service.application.service.UserService;
import com.user_service.presentation.dto.response.UserInternalResponse;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * Internal API Controller
 * 다른 마이크로서비스에서 호출하는 내부 전용 API
 *
 * 특징:
 * - Gateway 레벨에서 외부 접근 차단
 * - 서비스 간 통신 전용
 * - 인증 없이 호출 가능 (내부망에서만)
 */
@Slf4j
@RestController
@RequestMapping("/internal/users")
@RequiredArgsConstructor
public class UserInternalController {

	private final UserService userService;

	/**
	 * 단일 유저 조회
	 */
	@GetMapping("/{userId}")
	public UserInternalResponse getUserInfo(@PathVariable Long userId) {
		log.info("내부 API - 유저 정보 조회: userId={}", userId);
		return userService.getInternalUserInfo(userId);
	}

	/**
	 * 유저 일괄 조회
	 */
	@PostMapping
	public Map<Long, UserInternalResponse> getUsersBatch(
		@RequestBody List<Long> userIds
	) {
		log.info("내부 API - 유저 일괄 조회: count={}", userIds != null ? userIds.size() : 0);

		if (userIds == null || userIds.isEmpty()) {
			return Map.of();
		}

		return userService.getUsersByIds(userIds);
	}
}