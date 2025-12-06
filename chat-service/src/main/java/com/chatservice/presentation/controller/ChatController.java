package com.chatservice.presentation.controller;

import com.chatservice.application.service.ChatService;
import com.chatservice.presentation.request.CreateChatRoomReqDto;
import com.chatservice.presentation.response.ChatMessageResDto;
import com.chatservice.presentation.response.ChatRoomCursorResDto;
import com.chatservice.presentation.response.CreateChatRoomResDto;
import com.common.response.CommonResponse;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import static com.common.response.SuccessCode.CREATED;
import static com.common.response.SuccessCode.DELETED;
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
    return CommonResponse.of(OK);
  }

  @PostMapping("/{chatRoomId}/reject")
  public CommonResponse<Void> rejectChatRoom(@PathVariable Long chatRoomId) {
    Long userId = 1L;
    chatService.rejectChatRoom(chatRoomId, userId);
    return CommonResponse.of(OK);
  }

  @GetMapping
  public CommonResponse<ChatRoomCursorResDto> getChatRoomList(@RequestParam(required = false) Long cursor) {
    Long userId = 1L;
    return CommonResponse.of(OK, chatService.getChatRoomList(userId, cursor));
  }

  @DeleteMapping("/{chatRoomId}/leave")
  public CommonResponse<Void> leaveChatRoom(@PathVariable Long chatRoomId) {
    Long userId = 1L;
    chatService.leaveChatRoom(chatRoomId, userId);
    return CommonResponse.of(DELETED);
  }

  @DeleteMapping("/{chatRoomId}/messages/{messageId}")
  public CommonResponse<Void> deleteMessage(@PathVariable Long chatRoomId, @PathVariable Long messageId) {
    Long userId = 1L;
    chatService.deleteMessage(chatRoomId, messageId, userId);
    return CommonResponse.of(DELETED);
  }

  @GetMapping("/{chatRoomId}/messages")
  public CommonResponse<List<ChatMessageResDto>> getChatRoomMessages(@PathVariable Long chatRoomId, @RequestParam(required = false) Long before) {
    Long userId = 1L;
    return CommonResponse.of(OK, chatService.getChatRoomMessages(userId, chatRoomId, before));
  }
}

// TODO:
//  - 헤더에서 사용자 정보 가져오기
