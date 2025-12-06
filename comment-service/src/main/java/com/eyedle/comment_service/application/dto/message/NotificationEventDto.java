package com.eyedle.comment_service.application.dto.message;

import com.eyedle.comment_service.domain.model.Comment;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NotificationEventDto {

	private Long receiverId;
	private Long targetId;
	private Long subTargetId;
	private String type;
	private String message;

	public static NotificationEventDto toEvent(Comment comment, Long receiverId) {
		String authorName = comment.getAuthor().getName();
		String type = "FEED_COMMENT";
		String message  = authorName + "님이 회원님의 게시글에 댓글을 남겼습니다.";

		Long subTargetId = comment.getId();

		if (comment.getParentId() != null) {
			type = "COMMENT_REPLY";
			message = authorName + "님이 회원님의 댓글에 답글을 남겼습니다";
			subTargetId = comment.getParentId();
		}

		return NotificationEventDto.builder()
			.receiverId(receiverId)
			.type(type)
			.message(message)
			.targetId(comment.getFeedId())
			.subTargetId(subTargetId)
			.build();

	}

}
