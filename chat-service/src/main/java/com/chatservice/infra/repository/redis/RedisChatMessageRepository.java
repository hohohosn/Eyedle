package com.chatservice.infra.repository.redis;

import com.chatservice.domain.model.ChatMessage;
import com.chatservice.presentation.response.ChatMessageResDto;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class RedisChatMessageRepository {

  private final RedisTemplate<String, Object> redisTemplate;

  private String zsetKey(Long chatRoomId) {
    return "chat:messages:z:" + chatRoomId;
  }

  private String hashKey(Long chatRoomId) {
    return "chat:messages:h:" + chatRoomId;
  }

  public List<ChatMessageResDto> findRecentMessage(Long chatRoomId, long beforeEpochMs, int limit) {

    Set<Object> rawMessages = redisTemplate.opsForZSet().reverseRangeByScore(zsetKey(chatRoomId), 0, beforeEpochMs, 0, limit);

    if (rawMessages == null) {
      return List.of();
    }

    return rawMessages.stream()
        .filter(ChatMessageResDto.class::isInstance)
        .map(ChatMessageResDto.class::cast)
        .map(this::convertIfDeleted)
        .toList();
  }

  public void deleteMessage(Long chatRoomId, Long messageId, LocalDateTime deletedAt) {

    String zsetKey = zsetKey(chatRoomId);
    String hashKey = hashKey(chatRoomId);

    //  hash에서 원본 json 조회
    Object rawMessage = redisTemplate.opsForHash().get(hashKey, messageId.toString());
    if (rawMessage == null) {
      return;   // 없는 메시지
    }

    ChatMessageResDto messageResDto = (ChatMessageResDto) rawMessage;

    // 이미 삭제된 메세지면 deletedAt 유지
    ChatMessageResDto deletedMessage = new ChatMessageResDto(
        messageResDto.messageId(),
        messageResDto.senderId(),
        null,
        "삭제된 메시지입니다.",
        messageResDto.createdAt(),
        messageResDto.deletedAt() != null ? messageResDto.deletedAt() : deletedAt
    );

    // zset 업데이트
    Double score = redisTemplate.opsForZSet().score(zsetKey, rawMessage);

    if (score != null) {
      redisTemplate.opsForZSet().remove(zsetKey, rawMessage);
      redisTemplate.opsForZSet().add(zsetKey, deletedMessage, score);
    }

    // hash 업데이트
    redisTemplate.opsForHash().put(hashKey, messageId.toString(), deletedMessage);
  }

  public void saveMessage(ChatMessage chatMessage) {
    ChatMessageResDto chatMessageResDto = ChatMessageResDto.from(chatMessage);

    double score = chatMessage.getCreatedAt().atZone(ZoneOffset.UTC).toInstant().toEpochMilli();

    redisTemplate.opsForZSet().add(zsetKey(chatMessage.getChatRoomId()), chatMessageResDto, score);

    redisTemplate.opsForHash().put(hashKey(chatMessage.getChatRoomId()), chatMessageResDto.messageId().toString(), chatMessageResDto);
  }

  private ChatMessageResDto convertIfDeleted(ChatMessageResDto resDto) {
    if (resDto.deletedAt() == null) {
      return resDto;
    }

    return new ChatMessageResDto(
        resDto.messageId(),
        resDto.senderId(),
        null,
        "삭제된 메시지입니다.",
        resDto.createdAt(),
        resDto.deletedAt()
    );
  }
}
