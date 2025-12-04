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
	private static final Duration TTL = Duration.ofHours(1);

	private String getKey(Long userId) {
		return "user:profile:" + userId;
	}

	public Optional<Author> getAuthor(Long userId) {
		Object data = redisTemplate.opsForValue().get(getKey(userId));
		return Optional.ofNullable((Author) data);
	}

	public void saveAuthor(Author author) {
		if(author != null) {
			redisTemplate.opsForValue().set(getKey(author.getId()), author, TTL);
		}
	}

}
