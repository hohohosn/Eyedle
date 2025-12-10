package com.chatservice.common;

import com.common.response.BaseErrorCode;
import lombok.Getter;

@Getter
public enum ChatErrorCode implements BaseErrorCode {

  // ChatRoom
  CHAT_ROOM_NOT_FOUND("CHAT0000", "채팅방을 찾을 수 없습니다.", 404),
  SELF_CHAT_NOT_ALLOWED("CHAT0001", "자기 자신과의 채팅방은 만들 수 없습니다.", 400),
  CHATROOM_PARTICIPANT_LIMIT_EXCEEDED("CHAT0002", "채팅방 참여자 수를 초과했습니다.", 409),
  ALREADY_OPEN_CHAT_ROOM_STATUS("CHAT0003", "이미 열린 채팅방입니다.", 409),
  CANNOT_OPEN_CHAT_ROOM_STATUS("CHAT0003", "채팅방을 열 수 없는 상태입니다.", 409),
  CANNOT_REJECT_CHAT_ROOM("CHAT0003", "채팅을 거절할 수 없는 상태입니다.", 409),

  // ChatParticipant
  CHAT_PARTICIPANT_NOT_FOUND("CHAT1000", "채팅 참여자를 찾을 수 없습니다.", 404),
  DUPLICATE_CHAT_PARTICIPANT("CHAT1001", "중복된 채팅 참여자입니다.", 409),
  ALREADY_LEFT_CHAT_ROOM("CHAT1002", "이미 채팅방을 나간 상태입니다.", 409),
  ALREADY_JOIN_CHAT_ROOM("CHAT1002", "이미 채팅방을 참여한 상태입니다.", 409),

  // ChatMessage
  CHAT_MESSAGE_NOT_FOUND("CHAT2000", "채팅 메시지를 찾을 수 없습니다.", 404),
  MESSAGE_NOT_IN_CHATROOM("CHAT2001", "이 채팅방의 메시지가 아닙니다.", 400),
  NOT_MESSAGE_OWNER("CHAT2002", "다른 사용자의 메시지는 삭제할 수 없습니다.", 403),
  MESSAGE_ALREADY_DELETED("CHAT2003", "이미 삭제된 메시지입니다.", 400),

  // User
  USER_NOT_FOUND("CHAT5000", "사용자를 찾을 수 없습니다.", 404),
  BLOCKED_USER("CHAT5001", "차단 상태입니다.", 403),

  ;

  private final String code;
  private final String message;
  private final int status;

  ChatErrorCode(String code, String message, int status) {
    this.code = code;
    this.message = message;
    this.status = status;
  }
}
