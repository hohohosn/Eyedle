package com.eyedle.comment_service.presentation.controller;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.eyedle.comment_service.application.command.CommentCreateCommand;
import com.eyedle.comment_service.application.service.CommentService;
import com.eyedle.comment_service.global.annotation.CurrentUser;
import com.eyedle.comment_service.global.dto.UserContext;
import com.eyedle.comment_service.presentation.dto.request.CommentCreateRequestDto;
import com.eyedle.comment_service.presentation.dto.response.CommentCreateResponseDto;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class CommentController {

	private final CommentService commentService;

	/**
	 * 댓글(대댓글) 등록 메소드. parentId가 있으면 대댓글
	 * @param feedId
	 * @param commentCreateRequestDto
	 * @param user
	 * @return CommentCreateResponseDto
	 *
	 */
	@PostMapping("/feeds/{feedId}/comments")
	public CommentCreateResponseDto createComment(
		@PathVariable Long feedId,
		@Valid @RequestBody CommentCreateRequestDto commentCreateRequestDto,
		@CurrentUser UserContext user
	) {

		CommentCreateCommand commentCreateCommand = commentCreateRequestDto.toCommand(feedId, user.getId());

		return commentService.saveComment(commentCreateCommand);
	}
}
