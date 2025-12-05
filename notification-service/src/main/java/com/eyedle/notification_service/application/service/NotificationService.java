package com.eyedle.notification_service.application.service;

import java.util.List;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import com.common.exception.CustomException;
import com.eyedle.notification_service.domain.model.Notification;
import com.eyedle.notification_service.domain.repository.NotificationRepository;
import com.eyedle.notification_service.infra.config.RedisConfig;
import com.eyedle.notification_service.infra.repository.SseEmitterRepository;
import com.eyedle.notification_service.presentation.dto.SliceResponse;
import com.eyedle.notification_service.presentation.dto.request.NotificationCreateRequestDto;
import com.eyedle.notification_service.presentation.dto.response.NotificationCreateResponseDto;
import com.eyedle.notification_service.presentation.dto.response.NotificationGetResponseDto;
import com.eyedle.notification_service.presentation.enums.NotificationErrorCode;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationService {

	private final NotificationPolicy  notificationPolicy;
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

		sendEmitter(userId, emitter, "connection","connected!");

		return emitter;

	}

	public void sendNotification(NotificationCreateRequestDto notificationCreateRequestDto) {
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

	public SliceResponse<NotificationGetResponseDto> getNotifications(Long id, Long cursor, int size) {
		List<Notification> notifications = notificationRepository.findAllByReceiverId(id,cursor,size);
		return convertToSlice(notifications, size);
	}

	@Transactional
	public void readNotification(Long userId, Long notificationId) {

		Notification notification = notificationRepository.findByIdAndDeletedAtIsNull(notificationId)
			.orElseThrow(()-> new CustomException(
			NotificationErrorCode.NOTIFICATION_NOT_FOUND));

		notificationPolicy.UserIsReceiver(userId, notification.getReceiverId());

		notification.markAsRead();

		notificationRepository.save(notification);

	}

	@Transactional
	public void readAllNotifications(Long userId) {
		notificationRepository.markAllAsRead(userId);
	}

	private void sendEmitter(Long userId, SseEmitter emitter, String eventName, Object data) {
		try {
			emitter.send(
				SseEmitter.event()
					.name(eventName)
					.data(data));
		} catch (Exception e) {
			emitterRepository.deleteById(userId);
			log.error("SSE emitter 연결 실패", e);
			throw new RuntimeException("SSE emitter 연결에 실패했습니다.", e);
		}
	}

	private SliceResponse<NotificationGetResponseDto> convertToSlice(List<Notification> notifications, int size) {
		boolean hasNext = false;
		Long nextCursor = null;

		if (notifications.size() > size) {
			hasNext = true;
			notifications.remove(size);
		}

		if (!notifications.isEmpty()) {
			nextCursor = notifications.get(notifications.size() - 1).getId();
		}

		List<NotificationGetResponseDto> dtos = notifications.stream()
			.map(NotificationGetResponseDto::fromEntity)
			.toList();

		return SliceResponse.of(dtos, hasNext, nextCursor);
	}
}
