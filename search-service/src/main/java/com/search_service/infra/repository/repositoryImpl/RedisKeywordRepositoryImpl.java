package com.search_service.infra.repository.repositoryImpl;

import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.data.redis.connection.zset.Aggregate;
import org.springframework.data.redis.connection.zset.Weights;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Repository;

import com.search_service.domain.model.KeywordScore;
import com.search_service.domain.repository.KeywordRepository;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class RedisKeywordRepositoryImpl implements KeywordRepository {

	private final RedisTemplate<String, String> redisTemplate;

	private static final String KEYWORD_RANKING_KEY = "search:ranking";

	@Override
	public void incrementScore(String keyword){
		redisTemplate.opsForZSet().incrementScore(KEYWORD_RANKING_KEY, keyword, 1.0);
	}

	@Override
	public List<KeywordScore> getTopKeywords(int limit){
		Set<ZSetOperations.TypedTuple<String>> results = redisTemplate.opsForZSet()
			.reverseRangeWithScores(KEYWORD_RANKING_KEY, 0, limit-1);

		if(results == null) {
			return Collections.emptyList();
		}

		return results.stream()
			.map(tuple -> new KeywordScore(tuple.getValue(), tuple.getScore()))
			.collect(Collectors.toList());
	}

	@Override
	public void decayKeywordScores() {
		ZSetOperations<String, String> zSetOps = redisTemplate.opsForZSet();

		zSetOps.unionAndStore(
			KEYWORD_RANKING_KEY,
			Collections.emptyList(),
			KEYWORD_RANKING_KEY,
			Aggregate.SUM,
			Weights.of(0.5)
		);

		zSetOps.removeRangeByScore(KEYWORD_RANKING_KEY, 0, 0.9);
	}
}
