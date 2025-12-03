package com.chatservice.common;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.NOT_FOUND;

import com.common.response.BaseErrorCode;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum ChatErrorCode implements BaseErrorCode {

  // ChatRoom
  CHAT_ROOM_NOT_FOUND("CHAT0000", "채팅방을 찾을 수 없습니다.", NOT_FOUND),
  SELF_CHAT_NOT_ALLOWED("CHAT0001", "자기 자신과의 채팅방은 만들 수 없습니다.", BAD_REQUEST),

  // ChatParticipate
  CHAT_PARTICIPATE_NOT_FOUND("CHAT0010", "채팅 참여자를 찾을 수 없습니다.", NOT_FOUND),

  ;

  private final String code;
  private final String message;
  private final HttpStatus status;

  ChatErrorCode(String code, String message, HttpStatus status) {
    this.code = code;
    this.message = message;
    this.status = status;
  }
}
