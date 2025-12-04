package com.user_service.domain.repository;

import com.user_service.domain.model.User;
import com.user_service.domain.model.UserRole;
import com.user_service.domain.model.UserStatus;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

	/**
	 * 이메일로 사용자 조회
	 */
	Optional<User> findByEmail(String email);

	/**
	 * 사용자명으로 사용자 조회
	 */
	Optional<User> findByUsername(String username);

	/**
	 * ID로 사용자 조회 (삭제되지 않은 사용자만)
	 */
	Optional<User> findByIdAndIsDeletedFalse(Long id);

	/**
	 * 이메일 중복 체크
	 */
	boolean existsByEmail(String email);

	/**
	 * 사용자명 중복 체크
	 */
	boolean existsByUsername(String username);

	/**
	 * 상태별 사용자 조회
	 */
	Page<User> findByStatus(UserStatus status, Pageable pageable);

	/**
	 * 권한별 사용자 조회
	 */
	Page<User> findByRole(UserRole role, Pageable pageable);

	/**
	 * 활성 사용자 목록 조회
	 */
	@Query("SELECT u FROM User u WHERE u.status = 'ACTIVE' AND u.isDeleted = false")
	Page<User> findActiveUsers(Pageable pageable);

	/**
	 * 이메일 또는 사용자명으로 검색
	 */
	@Query("SELECT u FROM User u WHERE " +
		"(u.email LIKE %:keyword% OR u.username LIKE %:keyword%) " +
		"AND u.isDeleted = false")
	Page<User> searchByKeyword(@Param("keyword") String keyword, Pageable pageable);

}