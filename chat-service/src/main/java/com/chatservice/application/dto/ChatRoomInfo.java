package com.chatservice.application.dto;

import com.chatservice.domain.model.ChatMessage;
import java.time.LocalDateTime;

public record ChatRoomInfo(
    Long chatRoomId,
    Long receiverId,
    String receiverUserName,
    String lastMessage,
    LocalDateTime lastMessageAt
) {

  public static ChatRoomInfo of(Long chatRoomId, UserInfo receiverInfo, ChatMessage lastMessage) {
    return new ChatRoomInfo(
        chatRoomId,
        receiverInfo.receiverId(),
        receiverInfo.receiverUserName(),
        lastMessage != null ? lastMessage.getMessageContent() : null,
        lastMessage != null ? lastMessage.getCreatedAt() : null
    );
  }
}
