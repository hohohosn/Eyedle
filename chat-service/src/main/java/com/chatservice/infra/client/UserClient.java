package com.chatservice.infra.client;

import com.chatservice.application.dto.UserInfo;
import java.util.List;
import java.util.Map;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "user-service", contextId = "userClient")
public interface UserClient {

  @GetMapping("/internal/users/{userId}")
  UserInfo getUserInfo(@PathVariable Long userId);

  @PostMapping("/internal/users")
  Map<Long, UserInfo> getUserInfos(@RequestBody List<Long> userIds);
}
