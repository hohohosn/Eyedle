package com.eyedle.comment_service.application.command;

import com.eyedle.comment_service.domain.model.Comment;
import com.eyedle.comment_service.domain.vo.Author;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class CommentCreateCommand {
	private Long feedId;
	private Long parentId;
	private Long userId;
	private String content;

	public Comment toEntity(Author author) {
		return Comment.builder()
			.feedId(this.feedId)
			.content(this.content)
			.author(author)
			.build();
	}
}
