package com.chatservice.infra.stomp;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@EnableWebSocketMessageBroker
@RequiredArgsConstructor
public class StompWebSocketConfig implements WebSocketMessageBrokerConfigurer {

  private final StompHandler stompHandler;

  @Override
  public void registerStompEndpoints(StompEndpointRegistry registry) {
//    // 클라이언트가 웹소켓에 연결하기 위해 접속할 경로
//    registry.addEndpoint("/chats")
//        // 모든 도메인의 websocket 접속 허용
//        .setAllowedOriginPatterns("*")
//        // websocket 사용할 수 없는 환경일 경우 대체 프로토콜로 자동 fallback
//        .withSockJS();

    // 클라이언트가 웹소켓에 연결하기 위해 접속할 경로
    registry.addEndpoint("/chat")
        // 모든 도메인의 websocket 접속 허용
        .setAllowedOriginPatterns("*");
  }

  @Override
  public void configureMessageBroker(MessageBrokerRegistry registry) {
    // /publish/1 형태로 메시지 발행해야 함을 설정
    // /publish로 시작하는 url 패턴으로 메시지가 발행되면 @Controller 객체의 @MessageMapping 메서드로 라우팅
    registry.setApplicationDestinationPrefixes("/pub");

    // /topic/1 형태로 메시지를 수신(subscribe)해야 함을 설정
    registry.enableSimpleBroker("/sub");
  }

  // 웹소켓 요청(connect, subscribe, disconnect) 등의 요청 시에는 http header 등 http 메시지를 넣어올 수 있고,
  // 이를 interceptor를 통해 가로채 토큰 등을 검증할 수 있음.
  @Override
  public void configureClientInboundChannel(ChannelRegistration registration) {
    registration.interceptors(stompHandler);
  }

}
