package com.presenceservice.presentation.controller;

import com.common.response.CommonResponse;
import com.presenceservice.application.service.PresenceService;
import com.presenceservice.domain.model.OnlineStatus;
import com.presenceservice.presentation.dto.OnlineStatusResDto;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import static com.common.response.SuccessCode.OK;

@RestController
@RequiredArgsConstructor
@RequestMapping("/internal/presences")    // 내부 조회용 컨트롤러
public class PresenceController {

  private final PresenceService presenceService;

  @PostMapping("/{userId}")
  public CommonResponse<Void> updateStatus(@PathVariable Long userId, @RequestParam OnlineStatus onlineStatus) {
    presenceService.updateStatus(userId, onlineStatus);
    return CommonResponse.of(OK);
  }

  @GetMapping("/{userId}")
  public CommonResponse<OnlineStatusResDto> getStatus(@PathVariable Long userId) {
    return CommonResponse.of(OK, presenceService.getStatus(userId));
  }
}
