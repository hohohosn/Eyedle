package com.search_service.infra.repository.elasticsearch;

import java.util.List;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

import com.search_service.domain.model.UserDocument;

@Repository
public interface EsUserSearchRepository extends ElasticsearchRepository<UserDocument, Long> {
	List<UserDocument> findByUsernameContaining(String keyword);
}
