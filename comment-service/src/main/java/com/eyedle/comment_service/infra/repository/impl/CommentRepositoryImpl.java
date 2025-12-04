package com.eyedle.comment_service.infra.repository.impl;

import static com.eyedle.comment_service.domain.model.QComment.*;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import com.eyedle.comment_service.domain.model.Comment;
import com.eyedle.comment_service.domain.repository.CommentRepository;
import com.eyedle.comment_service.infra.repository.jpa.CommentJpaRepository;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class CommentRepositoryImpl implements CommentRepository {

	private final CommentJpaRepository commentJpaRepository;
	private final JPAQueryFactory jpaQueryFactory;

	@Override
	public Comment save(Comment comment) {
		return commentJpaRepository.save(comment);
	}

	@Override
	public Optional<Comment> findByIdAndDeletedAtIsNull(Long id) {
		return commentJpaRepository.findByIdAndDeletedAtIsNull(id);
	}

	@Override
	public List<Comment> findAllByFeedId(Long feedId, Long cursor, Pageable pageable) {
		return jpaQueryFactory
			.selectFrom(comment)
			.where(
				comment.feedId.eq(feedId),       // 1. 해당 피드의 댓글
				comment.parentId.isNull(),       // 2. 대댓글 제외 (원댓글만)
				comment.deletedAt.isNull(),      // 3. 삭제 안 된 것
				ltCursorId(cursor)             // 4. 커서 조건
			)
			.orderBy(comment.id.desc())       // 5. 최신순 정렬
			.limit(pageable.getPageSize() + 1)       // 6. 다음 페이지 확인용으로 +1개 조회
			.fetch();
	}

	@Override
	public List<Comment> findAllByParentId(Long feedId, Long parentId, Long cursor, Pageable pageable) {
		return jpaQueryFactory.selectFrom(comment)
			.where(
				comment.feedId.eq(feedId), // 1. 해당 피드의 댓글
				comment.parentId.eq(parentId), // 2. 대댓글
				comment.deletedAt.isNull(), // 3. 삭제 안 된 것
				gtCursorId(cursor) // 4. 커서 조건
			)
			.orderBy(comment.id.asc()) // 5. 날짜순 정렬
			.limit(pageable.getPageSize() + 1) // 6. 다음 페이지 확인용으로 +1개 조회
			.fetch();
	}

	private BooleanExpression gtCursorId(Long cursor) {
		if (cursor == null) {
			return null; // null을 리턴하면 where절에서 무시
		}
		return comment.id.gt(cursor);
	}

	private BooleanExpression ltCursorId(Long cursor) {
		if (cursor == null) {
			return null; // null을 리턴하면 where절에서 무시
		}
		return comment.id.lt(cursor);
	}

}
