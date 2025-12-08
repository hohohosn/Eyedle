package com.chatservice.infra.config;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import java.nio.charset.StandardCharsets;
import javax.crypto.SecretKey;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class StompHandler implements ChannelInterceptor {

  //@Value("${jwt.secretKey}")
  private String key;

  @Override
  public Message<?> preSend(Message<?> message, MessageChannel channel) {
    final StompHeaderAccessor accessor = StompHeaderAccessor.wrap(message);

    if (StompCommand.CONNECT == accessor.getCommand()) {
      log.info("connect 요청 시 토큰 유효성 검증");

      String bearerToken = accessor.getFirstNativeHeader("Authorization");

      String token = null;
      if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
        token = bearerToken.substring(7);
      }
      SecretKey secretKey = Keys.hmacShaKeyFor(key.getBytes(StandardCharsets.UTF_8));

      Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(token);
      log.info("토큰 검증 완료");
    }
    return message;
  }


}
