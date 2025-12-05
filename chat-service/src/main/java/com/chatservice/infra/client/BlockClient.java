package com.chatservice.infra.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "user-service", contextId = "blockClient")
public interface BlockClient {

  @GetMapping("/internal/blocks/is-blocked")
  boolean isBlocked(@RequestParam("fromUserId") Long fromUserId, @RequestParam("toUserId") Long toUserId);
}
