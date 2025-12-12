package com.feed_service.infra.user;

import com.feed_service.infra.user.response.ApiResponseDto;
import com.feed_service.infra.user.response.UserInfoResponseDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "user-service", path = "/api/users")
public interface UserClient {

    @GetMapping("/{userId}")
    ApiResponseDto<UserInfoResponseDto> getUserInfo(@PathVariable Long userId);
}

