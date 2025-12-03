package com.eyedle.comment_service.infra.repository.jpa;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.eyedle.comment_service.domain.model.Comment;

public interface CommentJpaRepository extends JpaRepository<Comment, Long> {

	Optional<Comment> findByIdAndDeletedAtIsNull(Long id);
}
