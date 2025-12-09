package com.eyedle.comment_service.infra.client;

import java.util.List;
import java.util.Map;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import com.eyedle.comment_service.infra.client.dto.UserGetResultDto;

@FeignClient(name = "user-service", url = "/internal")
public interface UserClient {

	@GetMapping("/users/{userId}")
	UserGetResultDto getUser(@PathVariable("userId") Long userId);

	@PostMapping("/users")
	Map<Long, UserGetResultDto> getUsers(@RequestBody List<Long> userIds);

	@GetMapping("/followers/is-following")
	boolean isFollowing(@RequestParam("fromUserId") Long fromUserId, @RequestParam("toUserId") Long toUserId);

}
