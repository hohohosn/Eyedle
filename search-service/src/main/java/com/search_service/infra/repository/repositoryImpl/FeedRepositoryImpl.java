package com.search_service.infra.repository.repositoryImpl;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.search_service.domain.model.FeedDocument;
import com.search_service.domain.repository.FeedRepository;
import com.search_service.infra.repository.elasticsearch.EsFeedSearchRepository;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class FeedRepositoryImpl implements FeedRepository {
	private final EsFeedSearchRepository esFeedSearchRepository;

	@Override
	public FeedDocument save(FeedDocument feed) {
		return esFeedSearchRepository.save(feed);
	}

	@Override
	public List<FeedDocument> searchByKeyword(String keyword) {
		return esFeedSearchRepository.findByContentContainingOrTagsContaining(keyword, keyword);
	}

	@Override
	public void deleteAll() {
		esFeedSearchRepository.deleteAll();
	}
}
