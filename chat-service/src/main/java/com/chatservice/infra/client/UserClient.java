package com.chatservice.infra.client;

import com.chatservice.application.dto.UserInfoResDto;
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
  UserInfoResDto getUserInfo(@PathVariable Long userId);

  @PostMapping("/internal/users")
  Map<Long, UserInfoResDto> getUserInfos(@RequestBody List<Long> userIds);
}
