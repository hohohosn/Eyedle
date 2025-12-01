package com.search_service.infra.repository.elasticsearch;

import java.util.List;

import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

import com.search_service.domain.model.FeedDocument;

public interface EsFeedSearchRepository extends ElasticsearchRepository<FeedDocument, Long> {
	List<FeedDocument> findByContentContainingOrTagsContaining(String content, String tags);
}
