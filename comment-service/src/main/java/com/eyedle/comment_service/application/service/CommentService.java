package com.eyedle.comment_service.application.service;

import static com.eyedle.comment_service.presentation.enums.CommentErrorCode.*;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.common.exception.CustomException;
import com.common.response.CommonResponse;
import com.eyedle.comment_service.application.command.CommentCreateCommand;
import com.eyedle.comment_service.domain.model.Comment;
import com.eyedle.comment_service.domain.repository.CommentRepository;
import com.eyedle.comment_service.domain.service.CommentDomainService;
import com.eyedle.comment_service.domain.vo.Author;
import com.eyedle.comment_service.infra.client.FeedClient;
import com.eyedle.comment_service.infra.client.UserClient;
import com.eyedle.comment_service.infra.client.dto.FeedGetResult;
import com.eyedle.comment_service.infra.client.dto.UserGetResult;
import com.eyedle.comment_service.infra.repository.UserCacheRepository;
import com.eyedle.comment_service.presentation.dto.SliceResponse;
import com.eyedle.comment_service.presentation.dto.response.CommentCreateResponseDto;
import com.eyedle.comment_service.presentation.dto.response.CommentGetResponseDto;
import com.eyedle.comment_service.presentation.enums.CommentErrorCode;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CommentService {

	private final CommentRepository commentRepository;
	private final UserClient userClient;
	private final FeedClient feedClient;
	private UserCacheRepository userCacheRepository;
	private final CommentDomainService commentDomainService;

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
		List<Comment> comments = commentRepository.findAllByFeedId(feedId, cursor, pageable);
		return convertToSlice(comments, pageable);
	}

	@Transactional(readOnly = true)
	public SliceResponse<CommentGetResponseDto> getReplies(Long userId, Long feedId, Long commentId, Long cursor, Pageable pageable) {
		validateReply(commentId, feedId);
		List<Comment> replies = commentRepository.findAllByParentId(feedId, commentId, cursor, pageable);
		return convertToSlice(replies, pageable);

	}

	private SliceResponse<CommentGetResponseDto> convertToSlice(List<Comment> comments, Pageable pageable) {
		boolean hasNext = false;
		Long nextCursor = null;

		if (comments.size() > pageable.getPageSize()) {
			hasNext = true;
			comments.remove(pageable.getPageSize());
		}

		if (!comments.isEmpty()) {
			nextCursor = comments.get(comments.size() - 1).getId();
		}

		List<CommentGetResponseDto> dtoList = comments.stream()
			.map(comment -> {
				Author author = getUser(comment.getAuthor().getId());
				return CommentGetResponseDto.fromEntity(comment, author);
			})
			.toList();

		return SliceResponse.of(dtoList, hasNext, nextCursor);
	}

	private Author getUser(Long userId) {

		return userCacheRepository.getAuthor(userId)
			.orElseGet(() -> {
				UserGetResult userResult = userClient.getUser(userId).getData();
				Author newAuthor = userResult.toAuthor();
				// Redis 저장
				userCacheRepository.saveAuthor(newAuthor);
				return newAuthor;
			});
	}


	/**
	 * 피드 검증
	 * @param feedId
	 */
	private void validateFeed(Long feedId, Long userId) {

		CommonResponse<FeedGetResult> result = feedClient.getFeed(feedId);

		if (result == null || result.getData() == null) {
			throw new CustomException(FEED_NOT_FOUND);
		}

		FeedGetResult feedGetResult = result.getData();

		// todo: 권한 검증(친한친구/팔로워/전체/비공개)
		commentDomainService.validateFeed(feedId, userId, feedGetResult.getUserId(), feedGetResult.getPermission());

	}

	/**
	 * 대댓글 검증
	 * @param parentId
	 * @param feedId
	 */
	private void validateReply(Long parentId, Long feedId) {

		Comment parentComment = commentRepository.findByIdAndDeletedAtIsNull(parentId)
			.orElseThrow(()-> new CustomException(CommentErrorCode.COMMENT_NOT_FOUND));

		// todo: 권한 검증(친한친구/팔로워/전체/비공개)
		commentDomainService.validateReply(parentComment, feedId);

	}
}
