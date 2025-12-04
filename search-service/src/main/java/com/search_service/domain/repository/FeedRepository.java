package com.search_service.domain.repository;

import java.util.List;

import com.search_service.domain.model.FeedDocument;

public interface FeedRepository {
	FeedDocument save(FeedDocument feed);
	List<FeedDocument> searchByKeyword(String keyword);
	void deleteAll();
}
