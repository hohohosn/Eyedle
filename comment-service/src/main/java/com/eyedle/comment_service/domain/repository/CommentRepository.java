package com.eyedle.comment_service.domain.repository;

import java.util.Optional;

import com.eyedle.comment_service.domain.model.Comment;

public interface CommentRepository {

	Comment save(Comment comment);

	Optional<Comment> findByIdAndDeletedAtIsNull(Long commentId);
}
