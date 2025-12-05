package com.eyedle.notification_service.domain.repository;

import java.util.List;
import java.util.Optional;

import com.eyedle.notification_service.domain.model.Notification;

public interface NotificationRepository {

	Notification save(Notification notification);

	List<Notification> findAllByReceiverId(Long receiverId, Long cursor, int size);

	Optional<Notification> findByIdAndDeletedAtIsNull(Long id);

	void markAllAsRead(Long receiverId);
}
