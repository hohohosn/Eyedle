package com.eyedle.comment_service;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.time.LocalDateTime;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;

import com.eyedle.comment_service.application.command.CommentCreateCommand;
import com.eyedle.comment_service.application.service.CommentService;
import com.eyedle.comment_service.domain.model.Comment;
import com.eyedle.comment_service.domain.vo.Author;
import com.eyedle.comment_service.infra.client.dto.UserGetResultDto;
import com.eyedle.comment_service.presentation.controller.CommentController;
import com.eyedle.comment_service.presentation.dto.request.CommentCreateRequestDto;
import com.eyedle.comment_service.presentation.dto.response.CommentCreateResponseDto;
import com.fasterxml.jackson.databind.ObjectMapper;

@WebMvcTest(CommentController.class)
@DisplayName("Comment Controller Test")
public class CommentControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private ObjectMapper objectMapper;

	@MockitoBean
	private CommentService commentService;

	@Test
	@DisplayName("댓글 작성 성공")
	public void createCommentTestSuccess() throws Exception {
		// given
		Long feedId = 100L;
		Long userId = 9874L;
		Long commentId = 12345L;
		String userName = "tester";
		String profileUrl = "/tester.png";
		String content = "테스트 댓글";

		CommentCreateRequestDto commentCreateRequestDto = CommentCreateRequestDto.builder().content(content).build();

		CommentCreateCommand commentCreateCommand = commentCreateRequestDto.toCommand(feedId, userId);

		UserGetResultDto userGetResultDto = UserGetResultDto.builder()
			.id(userId)
			//.userId(userName)
			.profileImageUrl(profileUrl)
			.build();

		Author author = userGetResultDto.toAuthor();

		Comment comment = commentCreateCommand.toEntity(author);
		ReflectionTestUtils.setField(comment, "id", commentId);

		CommentCreateResponseDto commentCreateResponseDto = CommentCreateResponseDto.fromEntity(comment);

		given(commentService.saveComment(any(CommentCreateCommand.class)))
			.willReturn(commentCreateResponseDto);

		// when&then
		mockMvc.perform(post("/feeds/{feedId}/comments", feedId)
			.header("X-User-ID", userId)
			.contentType(MediaType.APPLICATION_JSON)
			.content(objectMapper.writeValueAsString(commentCreateRequestDto))
		).andDo(print())
			.andExpect(status().isOk()) // 200 OK 검증
			.andExpect(jsonPath("$.commentId").value(commentId))
			.andExpect(jsonPath("$.content").value(content));

	}

	@Test
	@DisplayName("대댓글 작성 성공: parentId가 포함될 경우 대댓글")
	void createReplyTestSuccess() throws Exception {
		// given
		Long feedId = 100L;
		Long userId = 9874L;
		Long parentId = 5555L; // 부모 댓글 ID
		Long replyId = 6666L;  // 대댓글 ID
		String content = "대댓글입니다.";

		CommentCreateRequestDto requestDto = CommentCreateRequestDto.builder()
			.content(content)
			.parentId(parentId)
			.build();

		CommentCreateResponseDto responseDto = CommentCreateResponseDto.builder()
			.commentId(replyId)
			.feedId(feedId)
			.content(content)
			.parentId(parentId)
			.createdAt(LocalDateTime.now())
			.build();

		given(commentService.saveComment(any(CommentCreateCommand.class)))
			.willReturn(responseDto);

		// when & then
		mockMvc.perform(post("/feeds/{feedId}/comments", feedId)
				.header("X-User-Id", userId)
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(requestDto)))
			.andDo(print())
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.commentId").value(replyId))
			.andExpect(jsonPath("$.content").value(content))
			.andExpect(jsonPath("$.parentId").value(parentId));
	}

}
