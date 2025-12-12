package com.eyedle.comment_service.infra.repository;

import java.time.Duration;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

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

	public void saveAll(List<Author> authors) {
		if (authors == null || authors.isEmpty()) return;

		Map<String, Object> batchMap = authors.stream()
			.collect(Collectors.toMap(author -> getKey(author.getId()), author -> author));

		// multiSet으로 한 번에 저장
		redisTemplate.opsForValue().multiSet(batchMap);

		// TTL
		batchMap.keySet().forEach(key -> redisTemplate.expire(key, TTL));
	}

	public Map<Long, Author> getAuthors(Set<Long> userIds) {
		if (userIds == null || userIds.isEmpty()) return Collections.emptyMap();

		// Redis Key 생성
		List<String> keys = userIds.stream()
			.map(this::getKey)
			.toList();

		List<Object> values = redisTemplate.opsForValue().multiGet(keys);

		Map<Long, Author> result = new HashMap<>();

		for (Object val : values) {
			if (val instanceof Author author) {
				result.put(author.getId(), author);
			}
		}
		return result;
	}

}
