package com.chatservice.infra.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "user-service", contextId = "blockClient")
public interface BlockClient {

  // 두 사람이 서로 차단 아닐 때만 true, 한 쪽이라도 차단한 경우 false
  @GetMapping("/internal/blocks/is-blocked")
  boolean isNotBlocked(@RequestParam("userId1") Long userId1, @RequestParam("userId2") Long userId2);
}
