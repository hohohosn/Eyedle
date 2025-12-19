package com.chatservice.infra.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "user-service", contextId = "followClient")
public interface FollowClient {

  // 한쪽이라도 팔로우 되어있는 경우 true, 양쪽 다 팔로우 아닐 때만 false
  @GetMapping("/internal/follows/is-following")
  boolean isFollowing(@RequestParam("fromUserId") Long userId1, @RequestParam("toUserId") Long userId2);
}
