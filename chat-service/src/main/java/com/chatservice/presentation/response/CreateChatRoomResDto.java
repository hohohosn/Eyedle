package com.chatservice.presentation.response;

import com.chatservice.domain.model.ChatRoom;
import com.chatservice.domain.model.ChatRoomStatus;

public record CreateChatRoomResDto(
    Long chatRoomId,
    ChatRoomStatus chatRoomStatus
) {

  public static CreateChatRoomResDto from(ChatRoom chatRoom) {
    return new CreateChatRoomResDto(
        chatRoom.getId(),
        chatRoom.getChatRoomStatus()
    );
  }
}
