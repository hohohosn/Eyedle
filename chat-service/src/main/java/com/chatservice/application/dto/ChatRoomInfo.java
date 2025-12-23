package com.chatservice.application.dto;

import com.chatservice.domain.model.ChatMessage;
import com.chatservice.domain.model.OnlineStatus;
import java.time.LocalDateTime;

public record ChatRoomInfo(
    Long chatRoomId,
    Long receiverId,
    String receiverUserName,
    String lastMessage,
    LocalDateTime lastMessageAt,
    OnlineStatus onlineStatus
) {

  public static ChatRoomInfo of(Long chatRoomId, UserInfo receiverInfo, ChatMessage lastMessage, OnlineStatus onlineStatus) {

    Long rId = (receiverInfo != null) ? receiverInfo.receiverId() : null;
    String rUserName = (receiverInfo != null) ? receiverInfo.receiverUserName() : "알 수 없는 사용자";

    return new ChatRoomInfo(
        chatRoomId,
        rId,
        rUserName,
        lastMessage != null ? lastMessage.getMessageContent() : null,
        lastMessage != null ? lastMessage.getCreatedAt() : null,
        onlineStatus
    );
  }
}
