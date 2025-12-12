package com.chatservice.presentation.response;

import com.chatservice.application.dto.ChatRoomInfo;
import java.util.List;

public record ChatRoomCursorResDto(
    List<ChatRoomInfo> chatRoomInfoList,
    Long nextCursor
) {

  public static ChatRoomCursorResDto of(List<ChatRoomInfo> chatRoomInfoList, Long nextCursor) {
    return new ChatRoomCursorResDto(
        chatRoomInfoList,
        nextCursor
    );
  }
}
