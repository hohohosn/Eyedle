package com.chatservice.infra.client;

import com.chatservice.application.dto.OnlineStatusResDto;
import com.chatservice.domain.model.OnlineStatus;
import java.util.List;
import java.util.Map;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

@FeignClient(name = "presence-service", contextId = "presenceClient")
public interface PresenceClient {

  @PostMapping("/internal/presences/{userId}")
  void updateStatus(@PathVariable("userId") Long userId, @RequestParam("onlineStatus") OnlineStatus onlineStatus);

  @GetMapping("/internal/presences/{userId}")
  OnlineStatusResDto getStatus(@PathVariable Long userId);

  @PostMapping("/internal/presences/users")
  Map<Long, OnlineStatusResDto> getStatuses(@RequestBody List<Long> userIds);
}
