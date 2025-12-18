package com.feed_service.infra.user;

import com.feed_service.infra.config.FeignConfig;
import com.feed_service.infra.user.dto.UserInfoResponseDto;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "user-service", path = "/internal", configuration = FeignConfig.class)
public interface UserClient {

	@GetMapping("/users/{userId}")
	UserInfoResponseDto getUser(@PathVariable("userId") Long userId);

}
