package com.eyedle.notification_service.application.scheduler;

import java.time.LocalDateTime;

import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.eyedle.notification_service.domain.model.Notification;
import com.mongodb.client.result.UpdateResult;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class NotificationScheduler {

	private final MongoTemplate mongoTemplate;

	@Scheduled(cron = "0 0 3 * * *")
	public void deleteOldNotifications() {
		log.info("[Batch] Deleting old notifications ...");

		LocalDateTime thirtyDaysAgo = LocalDateTime.now().minusDays(30);

		Query query = new Query(Criteria.where("createdAt").lte(thirtyDaysAgo)
			.and("deletedAt").isNull());

		Update update = new Update().set("deletedAt", LocalDateTime.now());

		UpdateResult result = mongoTemplate.updateMulti(query, update, Notification.class);

		log.info("[Batch] 완료. {}개 삭제", result.getModifiedCount());
	}

}
