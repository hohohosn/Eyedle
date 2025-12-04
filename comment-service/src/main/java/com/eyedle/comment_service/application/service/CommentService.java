package com.eyedle.comment_service.application.service;

import static com.eyedle.comment_service.presentation.enums.CommentErrorCode.*;

import java.util.List;
import java.util.function.BiFunction;

import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.common.exception.CustomException;
import com.common.response.CommonResponse;
import com.eyedle.comment_service.application.command.CommentCreateCommand;
import com.eyedle.comment_service.application.command.CommentDeleteCommand;
import com.eyedle.comment_service.application.command.CommentUpdateCommand;
import com.eyedle.comment_service.domain.model.Comment;
import com.eyedle.comment_service.domain.repository.CommentRepository;
import com.eyedle.comment_service.domain.service.CommentPolicy;
import com.eyedle.comment_service.domain.vo.Author;
import com.eyedle.comment_service.infra.client.FeedClient;
import com.eyedle.comment_service.infra.client.UserClient;
import com.eyedle.comment_service.infra.client.dto.FeedGetResultDto;
import com.eyedle.comment_service.infra.client.dto.UserGetResultDto;
import com.eyedle.comment_service.infra.repository.UserCacheRepository;
import com.eyedle.comment_service.presentation.dto.SliceResponse;
import com.eyedle.comment_service.presentation.dto.response.CommentCreateResponseDto;
import com.eyedle.comment_service.presentation.dto.response.CommentDeleteResponseDto;
import com.eyedle.comment_service.presentation.dto.response.CommentGetResponseDto;
import com.eyedle.comment_service.presentation.dto.response.CommentUpdateResponseDto;
import com.eyedle.comment_service.presentation.dto.response.MyCommentGetResponseDto;
import com.eyedle.comment_service.presentation.dto.response.ReplyGetResponseDto;
import com.eyedle.comment_service.presentation.enums.CommentErrorCode;
import com.querydsl.core.Tuple;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CommentService {

	private final CommentRepository commentRepository;
	private final UserClient userClient;
	private final FeedClient feedClient;
	private UserCacheRepository userCacheRepository;
	private final CommentPolicy commentPolicy;

	@Transactional
	public CommentCreateResponseDto saveComment(CommentCreateCommand commentCreateCommand) {

		validateFeed(commentCreateCommand.getFeedId(), commentCreateCommand.getUserId());

		if (commentCreateCommand.getParentId() != null) {
			validateReply(commentCreateCommand.getParentId(), commentCreateCommand.getFeedId());
		}

		Author author = getUser(commentCreateCommand.getUserId());

		Comment comment = commentCreateCommand.toEntity(author);

		Comment savedComment = commentRepository.save(comment);

		return CommentCreateResponseDto.fromEntity(savedComment);

	}

	@Transactional(readOnly = true)
	public SliceResponse<CommentGetResponseDto> getComments(Long userId, Long feedId, Long cursor, Pageable pageable) {
		validateFeed(feedId, userId);
		List<Tuple> comments = commentRepository.findAllByFeedId(feedId, cursor, pageable);
		return tuplesToSlice(comments, pageable);
	}

	@Transactional(readOnly = true)
	public SliceResponse<ReplyGetResponseDto> getReplies(Long userId, Long feedId, Long commentId, Long cursor, Pageable pageable) {
		validateReply(commentId, feedId);
		List<Comment> replies = commentRepository.findAllByParentId(feedId, commentId, cursor, pageable);
		return convertToSlice(replies, pageable, ReplyGetResponseDto::fromEntity);
	}

	@Transactional
	public CommentDeleteResponseDto deleteComment(CommentDeleteCommand commentDeleteCommand) {

		Comment comment = getComment(commentDeleteCommand.getCommentId());
		commentPolicy.validateAuthor(commentDeleteCommand.getUserId(), comment.getAuthor().getId());
		comment.softDelete(commentDeleteCommand.getUserId());
		Comment deletedComment = commentRepository.save(comment);
		return CommentDeleteResponseDto.fromEntity(deletedComment);

	}

	@Transactional
	public CommentUpdateResponseDto updateComment(CommentUpdateCommand commentUpdateCommand) {

		Comment comment = getComment(commentUpdateCommand.getCommentId());
		commentPolicy.validateAuthor(commentUpdateCommand.getUserId(), comment.getAuthor().getId());
		comment.updateContents(commentUpdateCommand.getComment());
		Comment updatedComment = commentRepository.save(comment);
		return CommentUpdateResponseDto.fromEntity(updatedComment);

	}

	@Transactional(readOnly = true)
	public SliceResponse<MyCommentGetResponseDto> getMyComments(Long userId, Long cursor, String sortBy, String keyword, Pageable pageable) {
		List<Comment> comments = commentRepository.findAllByMyComments(userId, cursor, sortBy, keyword, pageable);
		return convertToSlice(comments, pageable, MyCommentGetResponseDto::fromEntity);
	}

	/**
	 * 댓글 목록 처리(대댓글개수있음)
	 * @param results
	 * @param pageable
	 * @return
	 */
	private SliceResponse<CommentGetResponseDto> tuplesToSlice(List<Tuple> results, Pageable pageable) {
		boolean hasNext = false;
		Long nextCursor = null;

		if (results.size() > pageable.getPageSize()) {
			hasNext = true;
			results.remove(pageable.getPageSize());
		}

		if (!results.isEmpty()) {
			Comment lastComment = results.get(results.size() - 1).get(0, Comment.class);
			nextCursor = lastComment.getId();
		}

		// 3. 변환 (replyCount 포함)
		List<CommentGetResponseDto> dtos = results.stream()
			.map(tuple -> {
				Comment comment = tuple.get(0, Comment.class); // 댓글
				Long replyCount = tuple.get(1, Long.class); // 대댓글 카운트
				Author author = getUser(comment.getAuthor().getId());
				return CommentGetResponseDto.fromEntity(comment, author, replyCount);
			})
			.toList();

		return SliceResponse.of(dtos, hasNext, nextCursor);
	}

	/**
	 * 목록 처리(대댓글카운트없음). 대댓글, 내가쓴댓글 공용
	 * @param comments
	 * @param pageable
	 * @return
	 */
	private <T> SliceResponse<T> convertToSlice(List<Comment> comments, Pageable pageable, BiFunction<Comment, Author, T> mapper) {
		boolean hasNext = false;
		Long nextCursor = null;

		if (comments.size() > pageable.getPageSize()) {
			hasNext = true;
			comments.remove(pageable.getPageSize());
		}

		if (!comments.isEmpty()) {
			nextCursor = comments.get(comments.size() - 1).getId();
		}

		List<T> dtoList = comments.stream()
			.map(comment -> {
				Author author = getUser(comment.getAuthor().getId());
				return mapper.apply(comment, author);
			})
			.toList();

		return SliceResponse.of(dtoList, hasNext, nextCursor);
	}

	/**
	 *  Redis -> DB 조회
	 * @param userId
	 * @return
	 */
	private Author getUser(Long userId) {

		return userCacheRepository.getAuthor(userId)
			.orElseGet(() -> {
				UserGetResultDto userGetResultDto = userClient.getUser(userId).getData();
				Author newAuthor = userGetResultDto.toAuthor();
				// Redis 저장
				userCacheRepository.saveAuthor(newAuthor);
				return newAuthor;
			});
	}

	private Comment getComment(Long commentId) {
		return commentRepository.findByIdAndDeletedAtIsNull(commentId)
			.orElseThrow(()-> new CustomException(CommentErrorCode.COMMENT_NOT_FOUND));
	}

	/**
	 * 피드 검증
	 * @param feedId
	 */
	private void validateFeed(Long feedId, Long userId) {

		CommonResponse<FeedGetResultDto> result = feedClient.getFeed(feedId);

		if (result == null || result.getData() == null) {
			throw new CustomException(FEED_NOT_FOUND);
		}

		FeedGetResultDto feedGetResultDto = result.getData();

		// todo: 권한 검증(친한친구/팔로워/전체/비공개)
		commentPolicy.validateFeed(feedId, userId, feedGetResultDto.getUserId(), feedGetResultDto.getPermission());

	}

	/**
	 * 대댓글 검증
	 * @param parentId
	 * @param feedId
	 */
	private void validateReply(Long parentId, Long feedId) {

		Comment parentComment = getComment(parentId);

		// todo: 권한 검증(친한친구/팔로워/전체/비공개)
		commentPolicy.validateReply(parentComment, feedId);

	}
}
