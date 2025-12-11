package com.eyedle.comment_service.presentation.controller;

import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.common.response.CommonResponse;
import com.common.response.SuccessCode;
import com.eyedle.comment_service.application.command.CommentCreateCommand;
import com.eyedle.comment_service.application.command.CommentDeleteCommand;
import com.eyedle.comment_service.application.command.CommentUpdateCommand;
import com.eyedle.comment_service.application.service.CommentService;
import com.eyedle.comment_service.global.annotation.CurrentUser;
import com.eyedle.comment_service.global.dto.UserContext;
import com.eyedle.comment_service.presentation.dto.SliceResponse;
import com.eyedle.comment_service.presentation.dto.request.CommentCreateRequestDto;
import com.eyedle.comment_service.presentation.dto.request.CommentUpdateRequestDto;
import com.eyedle.comment_service.presentation.dto.response.CommentCreateResponseDto;
import com.eyedle.comment_service.presentation.dto.response.CommentDeleteResponseDto;
import com.eyedle.comment_service.presentation.dto.response.CommentGetResponseDto;
import com.eyedle.comment_service.presentation.dto.response.CommentUpdateResponseDto;
import com.eyedle.comment_service.presentation.dto.response.MyCommentGetResponseDto;
import com.eyedle.comment_service.presentation.dto.response.ReplyGetResponseDto;

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
	 * @param userId
	 * @return CommentCreateResponseDto
	 *
	 */
	@PostMapping("/feeds/{feedId}/comments")
	public CommonResponse<CommentCreateResponseDto> createComment(
		@PathVariable("feedId") Long feedId,
		@Valid @RequestBody CommentCreateRequestDto commentCreateRequestDto,
		@RequestHeader("X-User-Id") Long userId
	) {

		CommentCreateCommand commentCreateCommand = commentCreateRequestDto.toCommand(feedId, userId);
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
		@RequestHeader("X-User-Id") Long userId
	){
		SliceResponse<CommentGetResponseDto> result = commentService.getComments(userId, feedId, cursor, pageable);
		return CommonResponse.of(SuccessCode.OK, result);
	}

	@DeleteMapping("/feeds/{feedId}/comments/{commentId}")
	public CommonResponse<CommentDeleteResponseDto> deleteComment(@RequestHeader("X-User-Id") Long userId,
		@PathVariable("feedId") Long feedId,
		@PathVariable("commentId") Long commentId
	){
		CommentDeleteResponseDto deleteResponseDto = commentService.deleteComment(CommentDeleteCommand.toCommand(userId, feedId, commentId));
		return CommonResponse.of(SuccessCode.DELETED, deleteResponseDto);
	}

	@PutMapping("/feeds/{feedId}/comments/{commentId}")
	public CommonResponse<CommentUpdateResponseDto> updateComment(@RequestHeader("X-User-Id") Long userId,
		@PathVariable("feedId") Long feedId,
		@PathVariable("commentId") Long commentId,
		@Valid @RequestBody CommentUpdateRequestDto commentUpdateRequestDto
	){
		CommentUpdateResponseDto updateResponseDto = commentService.updateComment(CommentUpdateCommand.toCommand(commentId, feedId, userId, commentUpdateRequestDto.getContent()));
		return CommonResponse.of(SuccessCode.OK, updateResponseDto);
	}

	@GetMapping("/feeds/{feedId}/comments/{commentId}/replies")
	public CommonResponse<SliceResponse<ReplyGetResponseDto>> getReplies(
		@PathVariable("feedId") Long feedId,
		@PathVariable("commentId") Long commentId,
		@RequestParam(required = false) Long cursor,
		@PageableDefault(size = 10) Pageable pageable,
		@RequestHeader("X-User-Id") Long userId
	) {
		SliceResponse<ReplyGetResponseDto> result = commentService.getReplies(userId, feedId,commentId, cursor, pageable);
		return CommonResponse.of(SuccessCode.OK, result);
	}

	@GetMapping("/comments/my")
	public CommonResponse<SliceResponse<MyCommentGetResponseDto>> getMyComments(
		@RequestHeader("X-User-Id") Long userId,
		@RequestParam(required = false) Long cursor,
		@RequestParam(defaultValue = "desc") String sortBy,
		@RequestParam(required = false) String keyword,
		@PageableDefault(size = 10) Pageable pageable
	){
		SliceResponse<MyCommentGetResponseDto> result =
			commentService.getMyComments(userId, cursor, sortBy, keyword, pageable);
		return CommonResponse.of(SuccessCode.OK, result);
	}


}
