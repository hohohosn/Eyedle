package com.chatservice.presentation.response;

import com.chatservice.domain.model.ChatRoomStatus;

public record CreateChatRoomResDto(
    Long chatRoomId,
    ChatRoomStatus chatRoomStatus
) {

}
