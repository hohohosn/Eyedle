package com.chatservice.infra.stomp;

import com.chatservice.application.service.ChatService;
import java.security.Principal;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionConnectEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;
import org.springframework.web.socket.messaging.SessionSubscribeEvent;

// 스프링과 stomp는 기본적으로 세션관리를 자동(내부적)으로 처리
// 연결/해제 이벤트를 기록, 연결된 세션 수를 실시간으로 확인할 목적으로 이벤트 리스너 생성 -> 로그, 디버깅 목적
@Slf4j
@Component
@RequiredArgsConstructor
public class StompEventListener {

  private final Set<String> sessions = ConcurrentHashMap.newKeySet();
  private final ChatService chatService;

  @EventListener
  public void connectHandle(SessionConnectEvent event) {

    StompHeaderAccessor accessor = StompHeaderAccessor.wrap(event.getMessage());
    sessions.add(accessor.getSessionId());

    log.info("connect session ID : {}", accessor.getSessionId());
    log.info("total sessions : {}", sessions.size());
  }

  @EventListener
  public void disconnectHandle(SessionDisconnectEvent event) {

    StompHeaderAccessor accessor = StompHeaderAccessor.wrap(event.getMessage());
    sessions.remove(accessor.getSessionId());

    log.info("disconnect session ID : {}", accessor.getSessionId());
    log.info("total sessions : {}", sessions.size());
  }

  @EventListener
  public void subscribeHandle(SessionSubscribeEvent event) {

    StompHeaderAccessor accessor = StompHeaderAccessor.wrap(event.getMessage());
    String destination = accessor.getDestination();

    if (destination == null) {
      return;
    }

    // 구독한 주소가 맞는지 확인
    if (destination.startsWith("/sub/chat/")) {

      Long chatRoomId = Long.parseLong(destination.replace("/sub/chat/", ""));

      Principal principal = accessor.getUser();

      if (principal == null) {
        log.warn("인증 정보 없이 구독 요청");
        return;
      }

      Long userId = Long.parseLong(principal.getName());

      // 채팅방 구독 시점에 안 읽은 메시지 읽음 처리
      chatService.markMessageAsRead(chatRoomId, userId);

      log.info("{}가 채팅방 {} 구독", userId, chatRoomId);
    }
  }
}
