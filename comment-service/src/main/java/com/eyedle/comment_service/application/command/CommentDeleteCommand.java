package com.eyedle.comment_service.application.command;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CommentDeleteCommand {
	private Long userId;
	private Long feedId;
	private Long commentId;

	public static CommentDeleteCommand toCommand(Long userId, Long feedId, Long commentId) {
		return CommentDeleteCommand.builder()
			.userId(userId)
			.feedId(feedId)
			.commentId(commentId)
			.build();
	}
}
