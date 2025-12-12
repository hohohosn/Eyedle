package com.eyedle.comment_service.presentation.dto.request;

import com.eyedle.comment_service.application.command.CommentCreateCommand;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CommentCreateRequestDto {

	@NotBlank(message = "댓글 내용을 입력해주세요.")
	@Size(max = 1000, message = "댓글은 1000자 이하로 작성해주세요.")
	private String content;

	private Long parentId;

	public CommentCreateCommand toCommand(Long feedId, Long userId){
		return CommentCreateCommand.builder()
			.feedId(feedId)
			.userId(userId)
			.parentId(this.parentId)
			.content(this.content)
			.build();
	}

}
