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
public class CommentDeleteResponseDto {
	private Long commentId;
	private Long deletedBy;
	private LocalDateTime deletedAt;

	public static CommentDeleteResponseDto fromEntity(Comment comment) {
		return CommentDeleteResponseDto.builder()
			.commentId(comment.getId())
			.deletedAt(comment.getDeletedAt())
			.deletedBy(comment.getDeletedBy())
			.build();
	}
}
