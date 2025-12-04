package com.eyedle.comment_service.presentation.controller;

import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.common.response.CommonResponse;
import com.common.response.SuccessCode;
import com.eyedle.comment_service.application.command.CommentCreateCommand;
import com.eyedle.comment_service.application.service.CommentService;
import com.eyedle.comment_service.global.annotation.CurrentUser;
import com.eyedle.comment_service.global.dto.UserContext;
import com.eyedle.comment_service.presentation.dto.SliceResponse;
import com.eyedle.comment_service.presentation.dto.request.CommentCreateRequestDto;
import com.eyedle.comment_service.presentation.dto.response.CommentCreateResponseDto;
import com.eyedle.comment_service.presentation.dto.response.CommentGetResponseDto;

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
	public CommonResponse<CommentCreateResponseDto> createComment(
		@PathVariable("feedId") Long feedId,
		@Valid @RequestBody CommentCreateRequestDto commentCreateRequestDto,
		@CurrentUser UserContext user
	) {

		CommentCreateCommand commentCreateCommand = commentCreateRequestDto.toCommand(feedId, user.getId());
		CommentCreateResponseDto commentCreateResponseDto = commentService.saveComment(commentCreateCommand);

		return CommonResponse.of(SuccessCode.CREATED, commentCreateResponseDto);
	}

	/**
	 * 댓글 목록 조회
	 * @param feedId
	 * @param cursor
	 * @param pageable
	 * @return
	 */
	@GetMapping("/feeds/{feedId}/comments")
	public CommonResponse<SliceResponse<CommentGetResponseDto>> getComments(
		@PathVariable("feedId") Long feedId,
		@RequestParam(required = false) Long cursor,
		@PageableDefault(size = 10) Pageable pageable,
		@CurrentUser UserContext user
	){
		SliceResponse<CommentGetResponseDto> result = commentService.getComments(user.getUserId(), feedId, cursor, pageable);
		return CommonResponse.of(SuccessCode.OK, result);
	}

	@GetMapping("/feeds/{feedId}/comments/{commentId}/replies")
	public CommonResponse<SliceResponse<CommentGetResponseDto>> getReplies(
		@PathVariable("feedId") Long feedId,
		@PathVariable("commentId") Long commentId,
		@RequestParam(required = false) Long cursor,
		@PageableDefault(size = 10) Pageable pageable,
		@CurrentUser UserContext user
	) {
		SliceResponse<CommentGetResponseDto> result = commentService.getReplies(user.getUserId(), feedId,commentId, cursor, pageable);
		return CommonResponse.of(SuccessCode.OK, result);
	}
}
