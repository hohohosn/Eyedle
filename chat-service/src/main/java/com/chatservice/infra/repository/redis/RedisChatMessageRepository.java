package com.chatservice.infra.repository.redis;

import com.chatservice.domain.model.ChatMessage;
import com.chatservice.presentation.response.ChatMessageResDto;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import static com.chatservice.domain.model.ContentType.DELETED;

@Slf4j
@Component
@RequiredArgsConstructor
public class RedisChatMessageRepository {

  private final RedisTemplate<String, Object> redisTemplate;
  private final ObjectMapper objectMapper;

  // 순서, 페이징
  private String zsetKey(Long chatRoomId) {
    return "chat:messages:z:" + chatRoomId;
  }

  // 데이터 저장
  private String chatMessageKey(Long chatRoomId, Long chatMessageId) {
    return "chat:messages:" + chatRoomId + ":" + chatMessageId;
  }

  public List<ChatMessageResDto> findRecentMessage(Long chatRoomId, long beforeEpochMs, int limit) {

    Set<Object> messageIds = redisTemplate.opsForZSet().reverseRangeByScore(zsetKey(chatRoomId), 0, beforeEpochMs, 0, limit);

    if (messageIds == null || messageIds.isEmpty()) {
      return List.of();
    }

    return messageIds.stream()
        .map(id -> {
          String key = chatMessageKey(chatRoomId, Long.parseLong(id.toString()));
          Object raw = redisTemplate.opsForValue().get(key);

          if (raw == null) {
            redisTemplate.opsForZSet().remove(zsetKey(chatRoomId), id);   // TTL 만료 메세지 삭제
            return null;
          }
          return objectMapper.convertValue(raw, ChatMessageResDto.class);
        })
        .filter(Objects::nonNull)
        .map(obj -> objectMapper.convertValue(obj, ChatMessageResDto.class))
        .map(this::convertIfDeleted)
        .toList();
  }

  public void deleteMessage(Long chatRoomId, Long messageId, LocalDateTime deletedAt) {

    String chatMessageKey = chatMessageKey(chatRoomId, messageId);
    Object rawMessage = redisTemplate.opsForValue().get(chatMessageKey);

    if (rawMessage == null) {
      return;
    }

    ChatMessageResDto messageResDto = objectMapper.convertValue(rawMessage, ChatMessageResDto.class);

    // 이미 삭제된 메세지면 deletedAt 유지
    ChatMessageResDto deletedMessage = new ChatMessageResDto(
        messageResDto.messageId(),
        messageResDto.senderId(),
        DELETED,
        null,
        messageResDto.createdAt(),
        messageResDto.deletedAt() != null ? messageResDto.deletedAt() : deletedAt
    );

    // TTL 설정 -> 없으면 3일, 있으면 기존 TTL 유지
    Duration ttl = redisTemplate.getExpire(chatMessageKey) > 0 ? Duration.ofSeconds(redisTemplate.getExpire(chatMessageKey)) : Duration.ofDays(3);

    redisTemplate.opsForValue().set(chatMessageKey, deletedMessage, ttl);
  }

  public void saveMessage(ChatMessage chatMessage) {

    ChatMessageResDto chatMessageResDto = ChatMessageResDto.from(chatMessage);

    double score = chatMessage.getCreatedAt().atZone(ZoneOffset.UTC).toInstant().toEpochMilli();

    // zset
    redisTemplate.opsForZSet().add(zsetKey(chatMessage.getChatRoomId()), chatMessageResDto.messageId().toString(), score);

    // 메시지 본문 dto
    redisTemplate.opsForValue()
        .set(chatMessageKey(chatMessage.getChatRoomId(), chatMessageResDto.messageId()), chatMessageResDto, Duration.ofDays(3));
  }

  private ChatMessageResDto convertIfDeleted(ChatMessageResDto resDto) {
    if (resDto.deletedAt() == null) {
      return resDto;
    }

    return new ChatMessageResDto(
        resDto.messageId(),
        resDto.senderId(),
        DELETED,
        null,
        resDto.createdAt(),
        resDto.deletedAt()
    );
  }
}
