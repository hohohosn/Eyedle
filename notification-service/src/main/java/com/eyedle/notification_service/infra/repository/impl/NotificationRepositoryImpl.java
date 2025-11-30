package com.eyedle.notification_service.infra.repository.impl;

import org.springframework.stereotype.Repository;

import com.eyedle.notification_service.domain.model.Notification;
import com.eyedle.notification_service.domain.repository.NotificationRepository;
import com.eyedle.notification_service.infra.repository.jpa.NotificationJpaRepository;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class NotificationRepositoryImpl implements NotificationRepository {

	private final NotificationJpaRepository notificationJpaRepository;

	@Override
	public Notification save(Notification notification) {
		return notificationJpaRepository.save(notification);
	}
}
