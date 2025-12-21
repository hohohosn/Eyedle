package com.presenceservice.application.service;

import com.presenceservice.domain.model.OnlineStatus;
import com.presenceservice.infra.repository.RedisPresenceRepository;
import com.presenceservice.presentation.dto.OnlineStatusResDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PresenceService {

  private final RedisPresenceRepository redisPresenceRepository;

  // 상태 업데이트
  public void updateStatus(Long userId, OnlineStatus onlineStatus) {
    redisPresenceRepository.saveStatus(userId, onlineStatus);
  }

  // 상태 조회
  public OnlineStatusResDto getStatus(Long userId) {
    return redisPresenceRepository.findStatusByUserId(userId);
  }
}
