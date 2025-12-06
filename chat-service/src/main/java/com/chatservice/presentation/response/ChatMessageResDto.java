package com.chatservice.presentation.response;

import com.chatservice.domain.model.ChatMessage;
import com.chatservice.domain.model.ContentType;
import java.time.LocalDateTime;

public record ChatMessageResDto(
    Long messageId,
    Long senderId,
    ContentType contentType,
    String messageContent,
    LocalDateTime createdAt,
    LocalDateTime deletedAt
) {

  public static ChatMessageResDto from(ChatMessage message) {
    return new ChatMessageResDto(
        message.getId(),
        message.getUserId(),
        message.getContentType(),
        message.getMessageContent(),
        message.getCreatedAt(),
        message.getDeletedAt()
    );
  }
}
