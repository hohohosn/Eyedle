package com.search_service.infra.repository.repositoryImpl;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.search_service.domain.model.UserDocument;
import com.search_service.domain.repository.UserRepository;
import com.search_service.infra.repository.elasticsearch.EsUserSearchRepository;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class UserRepositoryImpl implements UserRepository {

	private final EsUserSearchRepository esUserSearchRepository;

	@Override
	public UserDocument save(UserDocument user){
		return esUserSearchRepository.save(user);
	}

	@Override
	public List<UserDocument> searchByNickname(String keyword){
		return esUserSearchRepository.findByUserIdContaining(keyword);
	}

	@Override
	public void deleteAll(){
		esUserSearchRepository.deleteAll();
	}
}
