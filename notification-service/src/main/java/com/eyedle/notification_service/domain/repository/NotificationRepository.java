package com.eyedle.notification_service.domain.repository;

import com.eyedle.notification_service.domain.model.Notification;

public interface NotificationRepository {

	Notification save(Notification notification);

}
