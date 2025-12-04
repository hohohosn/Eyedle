package com.chatservice.presentation.controller;

import com.chatservice.application.service.ChatService;
import com.chatservice.presentation.request.CreateChatRoomReqDto;
import com.chatservice.presentation.response.ChatRoomCursorResDto;
import com.chatservice.presentation.response.CreateChatRoomResDto;
import com.common.response.CommonResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import static com.common.response.SuccessCode.CREATED;
import static com.common.response.SuccessCode.OK;

@RestController
@RequiredArgsConstructor
@RequestMapping("/chats")
public class ChatController {

  private final ChatService chatService;

  @PostMapping
  public CommonResponse<CreateChatRoomResDto> createDirectChatRoom(@RequestBody CreateChatRoomReqDto reqDto) {
    Long userId = 1L;
    return CommonResponse.of(CREATED, chatService.createDirectChatRoom(userId, reqDto));
  }

  @PostMapping("/{chatRoomId}/accept")
  public CommonResponse<Void> acceptChatRoom(@PathVariable Long chatRoomId) {
    Long userId = 1L;
    chatService.acceptChatRoom(chatRoomId, userId);
    return CommonResponse.of(OK, null);
  }

  @PostMapping("/{chatRoomId}/reject")
  public CommonResponse<Void> rejectChatRoom(@PathVariable Long chatRoomId) {
    Long userId = 1L;
    chatService.rejectChatRoom(chatRoomId, userId);
    return CommonResponse.of(OK, null);
  }

  @GetMapping
  public CommonResponse<ChatRoomCursorResDto> getChatRoomList(@RequestParam(required = false) Long cursor) {
    Long userId = 1L;
    return CommonResponse.of(OK, chatService.getChatRoomList(userId, cursor));
  }
}

// TODO:
//  - 헤더에서 사용자 정보 가져오기
