package com.chatservice.presentation.controller;

import com.chatservice.application.service.ChatService;
import com.chatservice.presentation.request.CreateChatRoomReqDto;
import com.chatservice.presentation.response.CreateChatRoomResDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/chats")
public class ChatController {

  private final ChatService chatService;

  @PostMapping
  public ResponseEntity<CreateChatRoomResDto> createDirectChatRoom(
      @RequestBody CreateChatRoomReqDto reqDto
  ) {
    Long userId = 1L;
    return ResponseEntity.ok(chatService.createDirectChatRoom(userId, reqDto));
  }
}

// TODO:
//  - 헤더에서 사용자 정보 가져오기
//  - 공통 응답 적용하기
