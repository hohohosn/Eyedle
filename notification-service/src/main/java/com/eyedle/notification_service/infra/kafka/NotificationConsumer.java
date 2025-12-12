package com.eyedle.notification_service.infra.kafka;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;

import com.eyedle.notification_service.application.service.NotificationService;
import com.eyedle.notification_service.presentation.dto.request.NotificationCreateRequestDto;
import com.eyedle.notification_service.presentation.dto.request.NotificationKafkaDto;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class NotificationConsumer {

	private final NotificationService notificationService;

	@KafkaListener(
		topics = "notification-topic",
		groupId = "notification-service-group",
		containerFactory = "kafkaListenerContainerFactory"
	)
	public void consumeNotification(NotificationKafkaDto event) {
		log.info("[Kafka] Received notification event {}", event);

		try {
			NotificationCreateRequestDto notificationCreateRequestDto = event.toRequestDto();

			notificationService.sendNotification(notificationCreateRequestDto);

			log.info("[Kafka] Notification sent successfully! to {} ", notificationCreateRequestDto.getReceiverIds());

		}catch (Exception e) {
			log.error("[Kafka] Notification sent failed! {} ", e.getMessage());
			throw e;
		}
	}

	@KafkaListener(topics = "notification-topic.DLT",
		groupId = "dlq-monitor-group",
		containerFactory = "dlqKafkaListenerContainerFactory"
	)
	public void listenDLQ(String message,
		@Header(KafkaHeaders.RECEIVED_TOPIC) String topic,
		@Header(KafkaHeaders.OFFSET) long offset) {
		log.warn("[DLQ] 메시지 수신!");
		log.warn("   -> Topic: {}, Offset: {}", topic, offset);
		log.warn("   -> Payload: {}", message);
		// TODO: 슬랙 알림 전송 or 관리자 이메일 발송
	}

}
