package com.presenceservice.infra.repository;

import com.presenceservice.domain.model.OnlineStatus;
import com.presenceservice.presentation.dto.OnlineStatusResDto;
import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class RedisPresenceRepository {

  private final RedisTemplate<String, Object> redisTemplate;

  private String presenceKey(Long userId) {
    return "presence:user:" + userId;
  }

  // 상태 저장
  public void saveStatus(Long userId, OnlineStatus onlineStatus) {
    String key = presenceKey(userId);

    Map<String, String> data = new HashMap<>();
    data.put("onlineStatus", onlineStatus.name());
    data.put("lastSeen", String.valueOf(System.currentTimeMillis()));

    redisTemplate.opsForHash().putAll(key, data);
    redisTemplate.expire(key, Duration.ofHours(1));    // 일정 시간 활동 없으면 만료
  }

  // 상태 조회
  public OnlineStatusResDto findStatusByUserId(Long userId) {
    String key = presenceKey(userId);

    Map<Object, Object> map = redisTemplate.opsForHash().entries(key);

    if (map == null || map.isEmpty()) {
      return OnlineStatusResDto.of(userId, OnlineStatus.OFFLINE, null);
    }

    OnlineStatus onlineStatus = OnlineStatus.valueOf((String) map.get("onlineStatus"));
    Long lastSeen = Long.parseLong((String) map.get("lastSeen"));

    return OnlineStatusResDto.of(userId, onlineStatus, lastSeen);
  }
}
