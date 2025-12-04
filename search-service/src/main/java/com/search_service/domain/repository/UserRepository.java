package com.search_service.domain.repository;

import java.util.List;

import com.search_service.domain.model.UserDocument;

public interface UserRepository {
	UserDocument save(UserDocument user);
	List<UserDocument> searchByNickname(String keyword);
	void deleteAll();
}
