package com.eyedle.notification_service.infra.repository.jpa;

import org.springframework.data.jpa.repository.JpaRepository;

import com.eyedle.notification_service.domain.model.Notification;

public interface NotificationJpaRepository extends JpaRepository<Notification,Long> {

	Notification save(Notification notification);

}
