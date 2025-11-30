package com.eyedle.notification_service.application.service;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import com.eyedle.notification_service.infra.repository.SseEmitterRepository;
import com.eyedle.notification_service.infra.repository.jpa.NotificationJpaRepository;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class NotificationService {

	private final NotificationJpaRepository notificationJpaRepository;
	private final SseEmitterRepository emitterRepository;
	private final StringRedisTemplate redisTemplate;
	private final ObjectMapper objectMapper;

	private static final Long DEFAULT_TIMEOUT = 60L * 1000 * 60;

	public SseEmitter subscribe(Long userId){

		SseEmitter emitter = new SseEmitter(DEFAULT_TIMEOUT);

		// callback
		emitter.onCompletion(() -> emitterRepository.deleteById(userId));
		emitter.onTimeout(() -> emitterRepository.deleteById(userId));
		emitter.onError((e) -> emitterRepository.deleteById(userId));

		emitterRepository.save(userId, emitter);

		// 503 error 방지
		try {
			emitter.send(
				SseEmitter.event()
				.name("connect")
				.data("connected!"));
		} catch (Exception e) {
			emitterRepository.deleteById(userId);
			throw new RuntimeException("연결 실패");
		}

		return emitter;

	}


}
