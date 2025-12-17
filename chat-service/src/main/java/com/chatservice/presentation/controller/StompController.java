package com.chatservice.presentation.controller;

import com.chatservice.application.service.ChatService;
import com.chatservice.presentation.request.ChatMessageReqDto;
import com.chatservice.presentation.response.ChatMessageResDto;
import java.security.Principal;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.stereotype.Controller;

@Controller
@RequiredArgsConstructor
public class StompController {

  private final SimpMessageSendingOperations messageTemplate;
  private final ChatService chatService;

  @MessageMapping("/{chatRoomId}")
  public void sendMessage(@DestinationVariable Long chatRoomId, ChatMessageReqDto reqDto, Principal principal) {

    Long userId = Long.valueOf(principal.getName());

    ChatMessageResDto resDto = chatService.saveChatMessage(chatRoomId, userId, reqDto);

    messageTemplate.convertAndSend("/sub/chats/" + chatRoomId, resDto);
  }
}
