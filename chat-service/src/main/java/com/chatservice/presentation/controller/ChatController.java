package com.chatservice.presentation.controller;

import com.chatservice.application.service.ChatService;
import com.chatservice.presentation.request.CreateChatRoomReqDto;
import com.chatservice.presentation.response.ChatMessageResDto;
import com.chatservice.presentation.response.ChatRoomCursorResDto;
import com.chatservice.presentation.response.CreateChatRoomResDto;
import com.common.response.CommonResponse;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
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
  public CommonResponse<CreateChatRoomResDto> createDirectChatRoom(
      @AuthenticationPrincipal Long userId,
      @RequestBody CreateChatRoomReqDto reqDto
  ) {
    return CommonResponse.of(CREATED, chatService.createDirectChatRoom(userId, reqDto));
  }

  @PostMapping("/{chatRoomId}/accept")
  public CommonResponse<Void> acceptChatRoom(
      @AuthenticationPrincipal Long userId,
      @PathVariable Long chatRoomId
  ) {
    chatService.acceptChatRoom(chatRoomId, userId);
    return CommonResponse.of(OK);
  }

  @PostMapping("/{chatRoomId}/reject")
  public CommonResponse<Void> rejectChatRoom(
      @AuthenticationPrincipal Long userId,
      @PathVariable Long chatRoomId
  ) {
    chatService.rejectChatRoom(chatRoomId, userId);
    return CommonResponse.of(OK);
  }

  @GetMapping
  public CommonResponse<ChatRoomCursorResDto> getChatRoomList(
      @AuthenticationPrincipal Long userId,
      @RequestParam(value = "cursor", required = false) Long cursor,
      @RequestParam(value = "pageSize", defaultValue = "10") int pageSize
  ) {
    return CommonResponse.of(OK, chatService.getChatRoomList(userId, cursor, pageSize));
  }

  @DeleteMapping("/{chatRoomId}/leave")
  public CommonResponse<Void> leaveChatRoom(
      @AuthenticationPrincipal Long userId,
      @PathVariable Long chatRoomId
  ) {
    chatService.leaveChatRoom(chatRoomId, userId);
    return CommonResponse.of(DELETED);
  }

  @DeleteMapping("/{chatRoomId}/messages/{messageId}")
  public CommonResponse<Void> deleteMessage(
      @AuthenticationPrincipal Long userId,
      @PathVariable Long chatRoomId,
      @PathVariable Long messageId
  ) {
    chatService.deleteMessage(chatRoomId, messageId, userId);
    return CommonResponse.of(DELETED);
  }

  @GetMapping("/{chatRoomId}/messages")
  public CommonResponse<List<ChatMessageResDto>> getChatRoomMessages(
      @AuthenticationPrincipal Long userId,
      @PathVariable Long chatRoomId,
      @RequestParam(value = "cursor", required = false) Long cursor,
      @RequestParam(value = "pageSize", defaultValue = "30") int pageSize
  ) {
    return CommonResponse.of(OK, chatService.getChatRoomMessages(userId, chatRoomId, cursor, pageSize));
  }

  @GetMapping("/{chatRoomId}/messages/more")
  public CommonResponse<List<ChatMessageResDto>> loadMoreChatMessages(
      @AuthenticationPrincipal Long userId,
      @PathVariable Long chatRoomId,
      @RequestParam(value = "cursor", required = false) Long cursor,
      @RequestParam(value = "dayRange", defaultValue = "7") int dayRange,
      @RequestParam(value = "pageSize", defaultValue = "30") int pageSize
  ) {
    return CommonResponse.of(OK, chatService.loadMoreChatMessages(userId, chatRoomId, cursor, dayRange, pageSize));
  }

  @GetMapping("/{chatRoomId}/unread")
  public CommonResponse<Long> countUnreadMessages(
      @AuthenticationPrincipal Long userId,
      @PathVariable Long chatRoomId
  ) {
    return CommonResponse.of(OK, chatService.countUnreadMessages(chatRoomId, userId));
  }
}
