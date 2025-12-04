package com.chatservice.infra.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "user-service")
public interface FollowClient {

  @GetMapping("/internal/follows/is-following")
  boolean isFollowing(@RequestParam("fromUserId") Long fromUserId, @RequestParam("toUserId") Long toUserId);
}
