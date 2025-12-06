package com.eyedle.comment_service.application.command;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CommentUpdateCommand {
	private Long commentId;
	private Long feedId;
	private Long userId;
	private String comment;

	public static CommentUpdateCommand toCommand(Long commentId, Long feedId, Long userId, String comment) {
		return CommentUpdateCommand.builder()
			.commentId(commentId)
			.feedId(feedId)
			.userId(userId)
			.comment(comment)
			.build();
	}
}
