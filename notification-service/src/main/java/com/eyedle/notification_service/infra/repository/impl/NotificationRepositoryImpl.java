package com.eyedle.notification_service.infra.repository.impl;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
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

	@Override
	public List<Notification> findAllByReceiverId(Long receiverId, Long cursor, int size) {

		Criteria criteria = new Criteria();

		criteria.and("receiverId").is(receiverId);
		criteria.and("deletedAt").isNull();
		LocalDateTime limitDate = LocalDateTime.now().minusDays(30);
		criteria.and("createdAt").gte(limitDate);

		if (cursor != null) {
			criteria.and("id").lte(cursor);
		}

		Query query = new Query(criteria);

		query.with(Sort.by(Sort.Direction.DESC, "id"));

		query.limit(size+1);

		return mongoTemplate.find(query, Notification.class);
	}

	@Override
	public Optional<Notification> findByIdAndDeletedAtIsNull(Long id) {
		return notificationMongoRepository.findByIdAndDeletedAtIsNull(id);
	}

	@Override
	public void markAllAsRead(Long userId) {
		Query query = new Query(Criteria.where("receiverId").is(userId)
			.and("deletedAt").isNull()
			.and("readAt").isNull());

		Update update = new Update().set("readAt", LocalDateTime.now());

		mongoTemplate.updateMulti(query, update, Notification.class);

	}
}
