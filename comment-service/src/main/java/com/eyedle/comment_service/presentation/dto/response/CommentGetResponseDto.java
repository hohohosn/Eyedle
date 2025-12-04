package com.eyedle.comment_service.presentation.dto.response;

import java.time.LocalDateTime;

import com.eyedle.comment_service.domain.model.Comment;
import com.eyedle.comment_service.domain.vo.Author;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CommentGetResponseDto {
	private Long commentId;
	private Long feedId;
	private Long parentId;
	private String content;
	private LocalDateTime createdAt;
	private AuthorDto author;

	@Getter
	@Builder
	@NoArgsConstructor
	@AllArgsConstructor
	public static class AuthorDto {
		private Long id;
		private String name;
		private String profileImg;

		public static AuthorDto fromEntity(Author author) {
			return AuthorDto.builder()
				.id(author.getId())
				.name(author.getName())
				.profileImg(author.getProfileImg())
				.build();
		}
	}

	public static CommentGetResponseDto fromEntity(Comment comment, Author author){
		return CommentGetResponseDto.builder()
			.commentId(comment.getId())
			.feedId(comment.getFeedId())
			.parentId(comment.getParentId())
			.content(comment.getContent())
			.createdAt(comment.getCreatedAt())
			.author(AuthorDto.fromEntity(author))
			.build();
	}


}
