package com.eyedle.comment_service.presentation.dto.response;

import java.time.LocalDateTime;

import com.eyedle.comment_service.domain.model.Comment;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CommentCreateResponseDto {
	private Long commentId;
	private Long parentId;
	private Long feedId;
	private String content;
	private Long createdBy;
	private String authorId;
	private String authorThumbnailUrl;
	private LocalDateTime createdAt;

	public static CommentCreateResponseDto fromEntity(Comment comment)
	{
		return CommentCreateResponseDto.builder()
			.commentId(comment.getId())
			.parentId(comment.getParentId())
			.feedId(comment.getFeedId())
			.content(comment.getContent())
			.createdBy(comment.getAuthor().getId())
			.createdAt(comment.getCreatedAt())
			.authorId(comment.getAuthor().getName())
			.authorThumbnailUrl(comment.getAuthor().getProfileImg())
			.build();
	}
}
