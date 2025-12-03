package com.eyedle.comment_service.infra.repository.impl;

import java.util.Optional;

import org.springframework.stereotype.Repository;

import com.eyedle.comment_service.domain.model.Comment;
import com.eyedle.comment_service.domain.repository.CommentRepository;
import com.eyedle.comment_service.infra.repository.jpa.CommentJpaRepository;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class CommentJpaRepositoryImpl implements CommentRepository {

	private final CommentJpaRepository commentJpaRepository;

	@Override
	public Comment save(Comment comment) {
		return commentJpaRepository.save(comment);
	}

	@Override
	public Optional<Comment> findByIdAndDeletedAtIsNull(Long commentId) {
		return commentJpaRepository.findByIdAndDeletedAtIsNull(commentId);
	}
}
