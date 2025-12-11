package com.search_service.infra.client;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import com.search_service.infra.client.dto.UserClientResponse;

@FeignClient(name = "user-service", path = "/user")
public interface UserFeignClient {

	@GetMapping("/{userId}")
	UserClientResponse getUserById(@PathVariable("userId") Long userId);

	@GetMapping
	List<UserClientResponse> getAllUsers(); // 파라미터(시간 등등) 추가해서 최근 10분에 대한 데이터만 반영하도록

	@GetMapping("/search/recent")
	List<UserClientResponse> searchRecentUsers(
		@RequestParam("keyword") String keyword,
		@RequestParam("since")LocalDateTime since
	);
}
