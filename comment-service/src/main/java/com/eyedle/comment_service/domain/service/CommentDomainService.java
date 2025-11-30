package com.eyedle.comment_service.domain.service;

import org.springframework.stereotype.Component;

import com.eyedle.comment_service.domain.model.Comment;

@Component
public class CommentDomainService {

	public void validateReply(Comment parentComment, Long feedId) {

		if (!parentComment.getFeedId().equals(feedId)) {
			throw new IllegalArgumentException("해당 피드에 존재하지 않는 댓글입니다.");
		}

		if (parentComment.getParentId() != null){
			throw new IllegalArgumentException("답글에는 더 이상 답글을 작성할 수 없습니다.");
		}

	}

}
