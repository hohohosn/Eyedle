package com.eyedle.notification_service.infra.repository.impl;

import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Repository;

import com.eyedle.notification_service.domain.model.Notification;
import com.eyedle.notification_service.domain.repository.NotificationRepository;
import com.eyedle.notification_service.infra.repository.mongo.NotificationMongoRepository;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class NotificationRepositoryImpl implements NotificationRepository {

	private final NotificationMongoRepository notificationMongoRepository;
	private final MongoTemplate mongoTemplate;

	@Override
	public Notification save(Notification notification) {
		return notificationMongoRepository.save(notification);
	}
}
