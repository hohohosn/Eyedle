package com.chatservice.infra.client;

import com.chatservice.application.dto.OnlineStatusResDto;
import com.chatservice.domain.model.OnlineStatus;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "presence-service", contextId = "presenceClient")
public interface PresenceClient {

  @PostMapping("/internal/presences/{userId}")
  void updateStatus(@PathVariable Long userId, @RequestParam("OnlineStatus") OnlineStatus status);

  @GetMapping("/internal/presences/{userId}")
  OnlineStatusResDto getStatus(@PathVariable Long userId);
}
