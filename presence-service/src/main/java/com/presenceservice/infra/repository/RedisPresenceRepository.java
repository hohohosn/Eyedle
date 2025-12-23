package com.presenceservice.infra.repository;

import com.presenceservice.domain.model.OnlineStatus;
import com.presenceservice.presentation.dto.OnlineStatusResDto;
import java.time.Duration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class RedisPresenceRepository {

  private final RedisTemplate<String, Object> redisTemplate;
  private static final String PRESENCE_CHANNEL = "presence-status-channel";

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

    // 접속 상태 변경 메시지 발행
    String message = userId + ":" + onlineStatus.name();
    redisTemplate.convertAndSend(PRESENCE_CHANNEL, message);
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

  // 상태 여러건 조회
  public Map<Long, OnlineStatusResDto> findStatusByUserIds(List<Long> userIds) {

    Map<Long, OnlineStatusResDto> resultMap = new HashMap<>();

    List<Object> resultList = redisTemplate.executePipelined((RedisCallback<Object>) connection -> {
      for (Long userId : userIds) {
        byte[] key = presenceKey(userId).getBytes();
        connection.hashCommands().hGetAll(key);
      }
      return null;
    });

    for (int i = 0; i < resultList.size(); i++) {
      Long userId = userIds.get(i);
      Map<Object, Object> map = (Map<Object, Object>) resultList.get(i);

      if (map == null || map.isEmpty()) {
        resultMap.put(userId, OnlineStatusResDto.of(userId, OnlineStatus.OFFLINE, null));
      } else {
        OnlineStatus onlineStatus = OnlineStatus.valueOf((String) map.get("onlineStatus"));
        Long lastSeen = Long.parseLong((String) map.get("lastSeen"));
        resultMap.put(userId, OnlineStatusResDto.of(userId, onlineStatus, lastSeen));
      }
    }

    return resultMap;
  }
}
