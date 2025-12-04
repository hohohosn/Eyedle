package com.eyedle.comment_service.domain.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Pageable;

import com.eyedle.comment_service.domain.model.Comment;
import com.querydsl.core.Tuple;


public interface CommentRepository {

	Comment save(Comment comment);

	Optional<Comment> findByIdAndDeletedAtIsNull(Long id);

	List<Tuple> findAllByFeedId(Long feedId, Long cursor, Pageable pageable);

	List<Comment> findAllByParentId(Long feedId, Long parentId, Long cursor, Pageable pageable);

	List<Comment> findAllByMyComments(Long userId, Long cursor, String sortBy, String keyword, Pageable pageable);

	void deleteAllRepliesByParentId(Long parentId, Long userId);
}
