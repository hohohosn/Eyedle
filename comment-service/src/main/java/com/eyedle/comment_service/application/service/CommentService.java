package com.eyedle.comment_service.application.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.eyedle.comment_service.application.command.CommentCreateCommand;
import com.eyedle.comment_service.domain.model.Comment;
import com.eyedle.comment_service.domain.repository.CommentRepository;
import com.eyedle.comment_service.domain.service.CommentDomainService;
import com.eyedle.comment_service.domain.vo.Author;
import com.eyedle.comment_service.infra.client.UserClient;
import com.eyedle.comment_service.infra.client.dto.UserGetResult;
import com.eyedle.comment_service.presentation.dto.response.CommentCreateResponseDto;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CommentService {

	private final CommentRepository commentRepository;
	private final UserClient userClient;
	private final CommentDomainService commentDomainService;

	@Transactional
	public CommentCreateResponseDto saveComment(CommentCreateCommand commentCreateCommand) {

		if (commentCreateCommand.getParentId() != null) {
			validateReply(commentCreateCommand.getParentId(), commentCreateCommand.getFeedId());
		}

		UserGetResult userResult = userClient.getUser(commentCreateCommand.getUserId());

		Author author = userResult.toAuthor();
		Comment comment = commentCreateCommand.toEntity(author);

		Comment savedComment = commentRepository.save(comment);

		return CommentCreateResponseDto.fromEntity(savedComment);

	}

	/**
	 * 대댓글 검증
	 * @param parentId
	 * @param feedId
	 */
	private void validateReply(Long parentId, Long feedId) {

		Comment parentComment = commentRepository.findByCommentIdAndDeletedAtIsNull(parentId)
			.orElseThrow(()-> new IllegalArgumentException("존재하지 않는 댓글에 대댓글을 작성할 수 없습니다"));

		commentDomainService.validateReply(parentComment, feedId);

	}
}
