package com.eyedle.comment_service.infra.repository;

import java.time.Duration;
import java.util.Optional;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import com.eyedle.comment_service.domain.vo.Author;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class UserCacheRepository {

	private final RedisTemplate<String, Object> redisTemplate;
	private static final Duration TTL = Duration.ofMinutes(30);

	private String getKey(Long userId) {
		return "user:profile:" + userId;
	}

	public void save(Author author) {
		if (author == null || author.getId() == null) {
			return;
		}
		redisTemplate.opsForValue().set(getKey(author.getId()), author, TTL);
	}

	public Optional<Author> get(Long userId) {
		if (userId == null) {
			return Optional.empty();
		}

		Object data = redisTemplate.opsForValue().get(getKey(userId));

		if (data instanceof Author) {
			return Optional.of((Author) data);
		}

		return Optional.empty();
	}

}
