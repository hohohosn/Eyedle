package com.eyedle.comment_service.domain.service;

import org.springframework.stereotype.Component;

import com.common.exception.CustomException;
import com.eyedle.comment_service.domain.model.Comment;
import com.eyedle.comment_service.presentation.enums.CommentErrorCode;

@Component
public class CommentPolicy {

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

	public void validateFeed(String permission, boolean isFollowing, boolean isFollowed) {

		switch (permission) {
			case "PUBLIC":
				//공개
				return;

			case "PRIVATE":
				// 비공개
				throw new CustomException(CommentErrorCode.COMMENT_FORBIDDEN);

			case "FOLLOWERS":
				// 팔로워 공개 : 댓글 작성자 -> 피드작성자 팔로우 상태
				if (!isFollowing) {
					throw new CustomException(CommentErrorCode.COMMENT_FORBIDDEN);
				}
				break;

			case "MUTUAL":
				// 맞팔 공개
				if (!isFollowing || !isFollowed) {
					throw new CustomException(CommentErrorCode.COMMENT_FORBIDDEN);
				}
				break;

			default:
				throw new CustomException(CommentErrorCode.COMMENT_FORBIDDEN);
		}

	}

}
