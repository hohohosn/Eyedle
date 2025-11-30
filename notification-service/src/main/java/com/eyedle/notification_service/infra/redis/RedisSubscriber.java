package com.eyedle.notification_service.infra.redis;

import java.io.IOException;
import java.util.Map;

import javax.imageio.IIOException;

import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import com.eyedle.notification_service.infra.repository.SseEmitterRepository;
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
			Map<String, Object> eventMap = objectMapper.readValue(message, Map.class);
			Long receiverId = Long.valueOf(String.valueOf(eventMap.get("receiverId")));

			SseEmitter emitter = emitterRepository.get(receiverId);

			if (emitter != null) {
				emitter.send(SseEmitter.event()
					.name("notification")
					.data(eventMap)
				);
				log.info("Sent message with receiverId={}", receiverId);
				log.info("Sent message with eventMap={}", eventMap);
			}
		}catch (IOException e){
			log.error("SSE 전송 중 에러", e);
		}
	}

}
