package com.search_service.infra.client;

import java.util.List;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import com.search_service.infra.client.dto.UserClientResponse;

@FeignClient(name = "user-service", path = "/user")
public interface UserFeignClient {

	@GetMapping("/{userId}")
	UserClientResponse getUserById(@PathVariable("userId") Long userId);

	@GetMapping
	List<UserClientResponse> getAllUsers();
}
