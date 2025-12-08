package com.chatservice.infra.repository.redis;

import com.chatservice.domain.model.ChatMessage;
import com.chatservice.presentation.response.ChatMessageResDto;
import com.common.exception.CustomException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import static com.chatservice.common.ChatErrorCode.MESSAGE_DESERIALIZATION_FAILED;
import static com.chatservice.common.ChatErrorCode.MESSAGE_SERIALIZATION_FAILED;

@Component
@RequiredArgsConstructor
public class RedisChatMessageRepository {

  private final RedisTemplate<String, String> redisTemplate;
  private final ObjectMapper objectMapper;

  private String zsetKey(Long chatRoomId) {
    return "chat:messages:z:" + chatRoomId;
  }

  private String hashKey(Long chatRoomId) {
    return "chat:messages:h:" + chatRoomId;
  }

  public List<ChatMessageResDto> findRecentMessage(Long chatRoomId, long beforeEpochMs, int limit) {

    Set<String> rawMessages = redisTemplate.opsForZSet().reverseRangeByScore(zsetKey(chatRoomId), 0, beforeEpochMs, 0, limit);

    if (rawMessages == null) {
      return List.of();
    }

    return rawMessages.stream()
        .map(json -> {
              try {
                return objectMapper.readValue(json, ChatMessageResDto.class);
              } catch (JsonProcessingException e) {
                throw new CustomException(MESSAGE_DESERIALIZATION_FAILED);
              }
            }
        )
        .map(msg -> {
          if (msg.deletedAt() != null) {
            return new ChatMessageResDto(
                msg.messageId(),
                msg.senderId(),
                null,
                "삭제된 메시지입니다.",
                msg.createdAt(),
                msg.deletedAt()
            );
          }
          return msg;
        })
        .toList();
  }

  public void deleteMessage(Long chatRoomId, Long messageId, LocalDateTime deletedAt) {

    String zsetKey = zsetKey(chatRoomId);
    String hashKey = hashKey(chatRoomId);

    //  hash에서 원본 json 조회
    String rawMessage = (String) redisTemplate.opsForHash().get(hashKey, messageId.toString());
    if (rawMessage == null) {
      return;   // 없는 메시지
    }

    ChatMessageResDto messageResDto;
    try {
      messageResDto = objectMapper.readValue(rawMessage, ChatMessageResDto.class);
    } catch (JsonProcessingException e) {
      throw new CustomException(MESSAGE_DESERIALIZATION_FAILED);
    }

    // 이미 삭제된 메세지면 deletedAt 유지
    ChatMessageResDto deletedMessage = new ChatMessageResDto(
        messageResDto.messageId(),
        messageResDto.senderId(),
        null,
        "삭제된 메시지입니다.",
        messageResDto.createdAt(),
        messageResDto.deletedAt() != null ? messageResDto.deletedAt() : deletedAt
    );

    // json 직렬화
    String newJson;
    try {
      newJson = objectMapper.writeValueAsString(deletedMessage);
    } catch (JsonProcessingException e) {
      throw new CustomException(MESSAGE_SERIALIZATION_FAILED);
    }

    // zset 업데이트
    Double score = redisTemplate.opsForZSet().score(zsetKey, rawMessage);
    if (score != null) {
      redisTemplate.opsForZSet().remove(zsetKey, rawMessage);
      redisTemplate.opsForZSet().add(zsetKey, newJson, score);
    }

    // hash 업데이트
    redisTemplate.opsForHash().put(hashKey, messageId.toString(), newJson);
  }

  public void saveMessage(ChatMessage chatMessage) {
    ChatMessageResDto chatMessageResDto = ChatMessageResDto.from(chatMessage);

    String json;
    try {
      json = objectMapper.writeValueAsString(chatMessageResDto);
    } catch (JsonProcessingException e) {
      throw new CustomException(MESSAGE_SERIALIZATION_FAILED);
    }

    redisTemplate.opsForZSet()
        .add(zsetKey(chatMessage.getChatRoomId()), json, chatMessage.getCreatedAt().atZone(ZoneOffset.UTC).toInstant().toEpochMilli());

    redisTemplate.opsForHash().put(hashKey(chatMessage.getChatRoomId()), chatMessageResDto.messageId().toString(), json);
  }
}
