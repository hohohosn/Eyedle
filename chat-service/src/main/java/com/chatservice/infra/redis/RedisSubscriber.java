package com.chatservice.infra.redis;

import com.chatservice.presentation.response.PresenceUpdateResDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class RedisSubscriber implements MessageListener {

  private final RedisTemplate<String, Object> redisTemplate;
  private final SimpMessagingTemplate simpMessagingTemplate;

  @Override
  public void onMessage(Message message, byte[] pattern) {
    try {
      String publishMessage = redisTemplate.getStringSerializer().deserialize(message.getBody());

      log.info("Redis Publish Message: {}", publishMessage);

      String payload = publishMessage.replace("\"", "");
      String[] split = payload.split(":");

      if (split.length < 2) {
        return;
      }

      Long userId = Long.parseLong(split[0]);
      String status = split[1];

      simpMessagingTemplate.convertAndSend("/sub/presence", new PresenceUpdateResDto(userId, status));
    } catch (Exception e) {
      log.error("Redis 메시지 처리 에러: {}", e.getMessage());
    }
  }

}
