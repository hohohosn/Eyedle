package com.chatservice.presentation.controller;

import com.chatservice.application.service.ChatService;
import com.chatservice.presentation.request.ChatMessageReqDto;
import com.chatservice.presentation.response.ChatMessageResDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.stereotype.Controller;

@Slf4j
@Controller
@RequiredArgsConstructor
public class StompController {

  private final SimpMessageSendingOperations messageTemplate;
  private final ChatService chatService;

  @MessageMapping("/{chatRoomId}")
  public void sendMessage(@DestinationVariable Long chatRoomId, ChatMessageReqDto reqDto) {
    Long userId = 1L;
    log.info("Sending message: {}", reqDto.messageContent());
    ChatMessageResDto resDto = chatService.saveChatMessage(chatRoomId, userId, reqDto);
    messageTemplate.convertAndSend("/sub/chats/" + chatRoomId, resDto);
  }

}
