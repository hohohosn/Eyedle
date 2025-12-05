package com.eyedle.notification_service.infra.repository.mongo;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.eyedle.notification_service.domain.model.Notification;

public interface NotificationMongoRepository extends MongoRepository<Notification, Long> {

}
