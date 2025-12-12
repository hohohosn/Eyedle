package com.search_service.domain.repository;

import java.util.List;

import com.search_service.domain.model.KeywordScore;

public interface KeywordRepository {
	void incrementScore(String keyword);
	List<KeywordScore> getTopKeywords(int limit);

	void decayKeywordScores();
}
