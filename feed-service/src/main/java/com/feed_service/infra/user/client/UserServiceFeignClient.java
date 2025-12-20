package com.feed_service.infra.user.client;

import com.feed_service.infra.config.FeignConfig;
import com.feed_service.infra.user.dto.UserInfoResponseDto;
import com.feed_service.infra.user.dto.UserInternalResponseDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;
import java.util.Set;

@FeignClient(name = "user-service", path = "/internal", configuration = FeignConfig.class
)
public interface UserServiceFeignClient {

    //유저 단건 조회
    @GetMapping("/users/{userId}")
    UserInfoResponseDto getUser(@PathVariable Long userId);

    //유저 다건 조회
    @PostMapping("/users/ids")
    List<UserInfoResponseDto> getUsersByIds(@RequestBody Set<Long> userIds);

    //팔로워 조회 (fan-out용)
    @GetMapping("/follows/{userId}/followers")
    List<UserInternalResponseDto> getFollowers(@PathVariable Long userId);
}