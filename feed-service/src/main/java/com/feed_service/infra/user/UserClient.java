package com.feed_service.infra.user;

import com.feed_service.infra.config.FeignConfig;
import com.feed_service.infra.user.dto.UserInfoResponseDto;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;
import java.util.Set;

@FeignClient(name = "user-service", path = "/internal", configuration = FeignConfig.class)
public interface UserClient {
	//단건 조회
	@GetMapping("/users/{userId}")
	UserInfoResponseDto getUser(@PathVariable("userId") Long userId);
	//다건 조회
	@PostMapping("/users/ids")
	List<UserInfoResponseDto> getUsersByIds(@RequestBody Set<Long> userIds);

}
