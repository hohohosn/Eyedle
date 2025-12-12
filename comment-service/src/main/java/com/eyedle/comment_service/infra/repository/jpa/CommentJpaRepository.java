package com.eyedle.comment_service.infra.repository.jpa;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.eyedle.comment_service.domain.model.Comment;


public interface CommentJpaRepository extends JpaRepository<Comment, Long> {

	Optional<Comment> findByIdAndDeletedAtIsNull(Long id);

	@Modifying(clearAutomatically = true) // 벌크 연산 후 영속성 컨텍스트 초기화
	@Query("UPDATE Comment c SET c.deletedAt = CURRENT_TIMESTAMP, c.deletedBy = :userId " +
		"WHERE c.parentId = :parentId AND c.deletedAt IS NULL")
	void deleteAllRepliesByParentId(@Param("parentId") Long parentId, @Param("userId") Long userId);
}
