package com.chatservice.infra.stomp;

import com.chatservice.infra.security.JwtProvider;
import com.common.exception.CustomException;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import static com.chatservice.common.ChatErrorCode.AUTHORIZATION_HEADER_MISSING;
import static com.chatservice.common.ChatErrorCode.CANNOT_EXTRACT_USER_ID_FROM_TOKEN;
import static com.chatservice.common.ChatErrorCode.INVALID_JWT_TOKEN;

@Slf4j
@Component
@RequiredArgsConstructor
public class StompHandler implements ChannelInterceptor {

  private final JwtProvider jwtProvider;

  @Override
  public Message<?> preSend(Message<?> message, MessageChannel channel) {

    StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);

    if (accessor == null) {
      return message;
    }

    // connect 요청일 때만 인증
    if (StompCommand.CONNECT.equals(accessor.getCommand())) {

      String authorization = accessor.getFirstNativeHeader("Authorization");

      if (authorization == null || !authorization.startsWith("Bearer ")) {
        throw new CustomException(AUTHORIZATION_HEADER_MISSING);
      }

      String token = authorization.substring(7);

      if (!jwtProvider.validateToken(token)) {
        throw new CustomException(INVALID_JWT_TOKEN);
      }

      Long userId = jwtProvider.extractUserId(token).orElseThrow(() -> new CustomException(CANNOT_EXTRACT_USER_ID_FROM_TOKEN));

      // 인증 객체 생성
      Authentication authentication = new UsernamePasswordAuthenticationToken(userId, null, List.of());

      // websocket 세션에 인증 정보 저장
      accessor.setUser(authentication);
      log.info("WebSocket 연결 인증 완료. 사용자 ID: {}", userId);

    }
    return message;
  }
}
