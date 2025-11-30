package com.eyedle.comment_service.infra.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

import com.eyedle.comment_service.infra.client.dto.UserGetResult;

@FeignClient(name = "user-service")
public interface UserClient {

	@GetMapping("/users/{userId}")
	UserGetResult getUser(Long userId);

}
