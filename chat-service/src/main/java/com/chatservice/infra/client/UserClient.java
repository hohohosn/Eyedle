package com.chatservice.infra.client;

import com.chatservice.application.dto.UserInfo;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "user-service")
public interface UserClient {

  @GetMapping("/internal/users/{userId}")
  UserInfo getUserInfo(@PathVariable Long userId);
}
