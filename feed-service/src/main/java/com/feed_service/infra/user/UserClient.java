package com.feed_service.infra.user;

import com.feed_service.infra.user.dto.ApiResponseDto;
import com.feed_service.infra.user.dto.UserInfoResponseDto;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;

@FeignClient(name = "user-service")
public interface UserClient {

	@GetMapping("/internal/users/{userId}")
	ApiResponseDto<UserInfoResponseDto> getUserInfo(
			@PathVariable Long userId,
			@RequestHeader("X-Internal-Call") String internalCall
	);
}

