package com.search_service.infra.repository.elasticsearch;

import java.util.List;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import com.search_service.domain.model.UserDocument;

public interface EsUserSearchRepository extends ElasticsearchRepository<UserDocument, Long> {
	List<UserDocument> findByUserIdContaining(String keyword);
}
