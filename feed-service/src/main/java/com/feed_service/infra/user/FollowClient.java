package com.feed_service.infra.user;

import com.feed_service.infra.user.dto.UserInternalResponseDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@FeignClient(name = "user-service", path = "/internal/follows")
public interface FollowClient {

    @GetMapping("/followers")
    List<UserInternalResponseDto> getFollowers(
            @RequestParam("userId") Long userId
    );
}
