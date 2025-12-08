package com.chatservice.infra.repository.redis;

import com.chatservice.domain.model.ChatMessage;
import com.chatservice.presentation.response.ChatMessageResDto;
import com.common.exception.CustomException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.LocalDateTime;
import java.time.ZoneId;
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

  private String key(Long chatRoomId) {
    return "chat:messages:" + chatRoomId;
  }

  public List<ChatMessageResDto> findRecentMessage(Long chatRoomId, long beforeEpochMs, int limit) {
    String key = key(chatRoomId);

    Set<String> rawMessages = redisTemplate.opsForZSet().reverseRangeByScore(key, 0, beforeEpochMs, 0, limit);

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
    String key = key(chatRoomId);

    Set<String> rawMessages = redisTemplate.opsForZSet().range(key, 0, -1);

    if (rawMessages == null) {
      return;
    }

    for (String rawMessage : rawMessages) {
      try {
        ChatMessageResDto messageResDto = objectMapper.readValue(rawMessage, ChatMessageResDto.class);

        if (messageResDto.messageId().equals(messageId)) {
          ChatMessageResDto deletedMessage = new ChatMessageResDto(
              messageResDto.messageId(),
              messageResDto.senderId(),
              null,
              "삭제된 메시지입니다.",
              messageResDto.createdAt(),
              deletedAt
          );

          String newJson = objectMapper.writeValueAsString(deletedMessage);

          Double score = redisTemplate.opsForZSet().score(key, rawMessage);

          if (score != null) {
            redisTemplate.opsForZSet().remove(key, rawMessage);
            redisTemplate.opsForZSet().add(key, newJson, score);
          }
          break;
        }
      } catch (JsonProcessingException e) {
        throw new CustomException(MESSAGE_DESERIALIZATION_FAILED);
      }
    }
  }

  public void saveMessage(ChatMessage chatMessage) {
    ChatMessageResDto chatMessageResDto = ChatMessageResDto.from(chatMessage);

    String json;
    try {
      json = objectMapper.writeValueAsString(chatMessageResDto);
    } catch (JsonProcessingException e) {
      throw new CustomException(MESSAGE_SERIALIZATION_FAILED);
    }

    redisTemplate.opsForZSet().add(
        key(chatMessage.getChatRoomId()),
        json,
        chatMessage.getCreatedAt().atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()
    );
  }

}
