package com.eyedle.comment_service.domain.service;

import org.springframework.stereotype.Component;

import com.common.exception.CustomException;
import com.eyedle.comment_service.domain.model.Comment;
import com.eyedle.comment_service.presentation.enums.CommentErrorCode;

@Component
public class CommentDomainService {

	public void validateReply(Comment parentComment, Long feedId) {

		if (!parentComment.getFeedId().equals(feedId)) {
			throw new CustomException(CommentErrorCode.COMMENT_FEED_MISMATCH);
		}

		if (parentComment.getParentId() != null){
			throw new CustomException(CommentErrorCode.REPLY_DEPTH_OVER);
		}

	}

	public void validateAuthor(Long userId, Long authorId){
		if (!userId.equals(authorId)){
			throw new CustomException(CommentErrorCode.AUTHOR_FORBIDDEN);
		}
	}

	public void validateFeed(Long feedId, Long requestUserId, Long feedAuthorId, String permission) {

	}

}
