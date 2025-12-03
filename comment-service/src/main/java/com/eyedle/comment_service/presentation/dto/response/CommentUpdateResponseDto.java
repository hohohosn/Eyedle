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
public class CommentUpdateResponseDto {
	private Long commentId;
	private String content;
	private LocalDateTime createdAt;
	private LocalDateTime updatedAt;

	public static CommentUpdateResponseDto fromEntity(Comment comment) {
		return CommentUpdateResponseDto.builder()
			.commentId(comment.getId())
			.content(comment.getContent())
			.createdAt(comment.getCreatedAt())
			.updatedAt(comment.getUpdatedAt())
			.build();
	}
}
