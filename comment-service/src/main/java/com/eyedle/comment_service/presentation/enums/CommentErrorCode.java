package com.eyedle.comment_service.presentation.enums;

import com.common.response.BaseErrorCode;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum CommentErrorCode implements BaseErrorCode {
	AUTHOR_FORBIDDEN(403, "E403", "댓글 수정, 삭제 권한이 없습니다."),
	USER_NOT_FOUND(404, "E404", "해당 회원을 찾을 수 없습니다."),
	FEED_NOT_FOUND(404, "E404", "존재하지 않거나 삭제된 피드입니다."),
	COMMENT_NOT_FOUND(404,"E404", "존재하지 않거나 삭제된 댓글입니다."),
	REPLY_DEPTH_OVER(400,"E400", "답글에는 더 이상 답글을 작성할 수 없습니다."),
	COMMENT_FEED_MISMATCH(400,"E400", "해당 피드에 존재하지 않는 댓글입니다."),
	COMMENT_FORBIDDEN(403, "E403", "댓글 접근 권한이 없습니다.");

	private final int status;
	private final String code;
	private final String message;

}
