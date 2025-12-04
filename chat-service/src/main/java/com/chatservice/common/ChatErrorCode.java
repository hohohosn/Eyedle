package com.chatservice.common;

import com.common.response.BaseErrorCode;
import lombok.Getter;

@Getter
public enum ChatErrorCode implements BaseErrorCode {

  // ChatRoom
  CHAT_ROOM_NOT_FOUND("CHAT0000", "채팅방을 찾을 수 없습니다.", 404),
  SELF_CHAT_NOT_ALLOWED("CHAT0001", "자기 자신과의 채팅방은 만들 수 없습니다.", 400),

  // ChatParticipate
  CHAT_PARTICIPATE_NOT_FOUND("CHAT1000", "채팅 참여자를 찾을 수 없습니다.", 404),
  DUPLICATE_CHAT_PARTICIPATE("CHAT1001", "중복된 채팅 참여자입니다.", 409),

  // USER
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
