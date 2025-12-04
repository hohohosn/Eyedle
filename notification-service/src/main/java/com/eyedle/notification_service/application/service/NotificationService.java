package com.eyedle.notification_service.application.service;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import com.eyedle.notification_service.domain.model.Notification;
import com.eyedle.notification_service.domain.repository.NotificationRepository;
import com.eyedle.notification_service.infra.config.RedisConfig;
import com.eyedle.notification_service.infra.repository.SseEmitterRepository;
import com.eyedle.notification_service.presentation.dto.request.NotificationCreateRequestDto;
import com.eyedle.notification_service.presentation.dto.response.NotificationCreateResponseDto;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationService {

	private final NotificationRepository notificationRepository;
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

	@Transactional
	public void send(NotificationCreateRequestDto notificationCreateRequestDto) {
		Notification notification = notificationCreateRequestDto.toEntity();
		Notification savedNotification = notificationRepository.save(notification);
		NotificationCreateResponseDto notificationCreateResponseDto = NotificationCreateResponseDto.fromEntity(savedNotification);

		try {
			String messageJson = objectMapper.writeValueAsString(notificationCreateResponseDto);
			redisTemplate.convertAndSend(RedisConfig.SSE_TOPIC, messageJson);
			log.info("Redis Published for user: {}", notificationCreateResponseDto.getReceiverId());
		} catch (JsonProcessingException e) {
			log.error("Redis 발송 실패",e);
		}

	}
}
