package com.feed_service.infra.user;

import com.user_service.presentation.dto.response.UserInternalResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@FeignClient(name = "user-service", path = "/internal/follows")
public interface FollowClient {

    @GetMapping("/followers")
    List<UserInternalResponse> getFollowers(
            @RequestParam("userId") Long userId
    );
}
