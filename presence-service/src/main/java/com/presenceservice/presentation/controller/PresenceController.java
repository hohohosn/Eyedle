package com.presenceservice.presentation.controller;

import com.presenceservice.application.service.PresenceService;
import com.presenceservice.domain.model.OnlineStatus;
import com.presenceservice.presentation.dto.OnlineStatusResDto;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/internal/presences")    // 내부 조회용 컨트롤러
public class PresenceController {

  private final PresenceService presenceService;

  @PostMapping("/{userId}")
  public void updateStatus(@PathVariable(value = "userId") Long userId, @RequestParam(name = "onlineStatus") OnlineStatus onlineStatus) {
    presenceService.updateStatus(userId, onlineStatus);
  }

  @GetMapping("/{userId}")
  public OnlineStatusResDto getStatus(@PathVariable Long userId) {
    return presenceService.getStatus(userId);
  }

  @PostMapping("/users")
  public Map<Long, OnlineStatusResDto> getOnlineStatus(@RequestBody List<Long> userIds) {
    return presenceService.getStatusByUserIds(userIds);
  }
}
