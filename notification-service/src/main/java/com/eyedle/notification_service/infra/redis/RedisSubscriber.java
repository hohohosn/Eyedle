package com.eyedle.notification_service.infra.redis;

import java.io.IOException;
import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import com.eyedle.notification_service.infra.repository.SseEmitterRepository;
import com.eyedle.notification_service.presentation.dto.request.NotificationRedisDto;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class RedisSubscriber {

	private final SseEmitterRepository emitterRepository;
	private final ObjectMapper objectMapper;

	public void sendMessage(String message) {
		try {
			log.info("🔥 [Redis Sub] Received: {}", message);
			NotificationRedisDto notificationRedisDto = objectMapper.readValue(message, NotificationRedisDto.class);
			Long receiverId = notificationRedisDto.getReceiverId();

			SseEmitter emitter = emitterRepository.get(receiverId);

			if (emitter != null) {
				emitter.send(SseEmitter.event()
					.name("notification")
					.data(notificationRedisDto.getMessage())
				);
				log.info("Sent message with receiverId={}", receiverId);
				log.info("Sent message with eventMap={}", notificationRedisDto);
			}
		}catch (IOException e){
			log.error("SSE 전송 중 에러", e);
		}
	}

}
