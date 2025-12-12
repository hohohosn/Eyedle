package com.eyedle.comment_service;

import static com.common.response.SuccessCode.*;
import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.*;

import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import com.common.response.CommonResponse;
import com.eyedle.comment_service.application.command.CommentCreateCommand;
import com.eyedle.comment_service.application.service.CommentService;
import com.eyedle.comment_service.domain.model.Comment;
import com.eyedle.comment_service.domain.repository.CommentRepository;
import com.eyedle.comment_service.domain.service.CommentPolicy;
import com.eyedle.comment_service.infra.client.FeedClient;
import com.eyedle.comment_service.infra.client.UserClient;
import com.eyedle.comment_service.infra.client.dto.FeedGetResultDto;
import com.eyedle.comment_service.infra.client.dto.UserGetResultDto;
import com.eyedle.comment_service.presentation.dto.response.CommentCreateResponseDto;

@ExtendWith(MockitoExtension.class)
public class CommentServiceTest {

	@InjectMocks
	private CommentService commentService;

	@Mock
	private CommentRepository commentRepository;

	@Mock
	private CommentPolicy commentPolicy;

	@Mock
	private UserClient userClient;

	@Mock
	private FeedClient feedClient;

	private void setupFeedClientMock(Long feedId) {
		FeedGetResultDto mockFeedResult = new FeedGetResultDto(feedId, 9000L,"PUBLIC");

		CommonResponse<FeedGetResultDto> response = CommonResponse.of(OK, mockFeedResult);

		given(feedClient.getFeed(feedId)).willReturn(response);
	}

	private void setupUserClientMock(Long userId) {
		UserGetResultDto mockUserResult = UserGetResultDto.builder()
			.id(userId)
			//.userName("tester")
			.profileImageUrl("/img.png")
			.build();

		given(userClient.getUser(userId)).willReturn(mockUserResult);

	}

	private void setupRepositorySaveMock(Long commentId) {
		given(commentRepository.save(any(Comment.class))).willAnswer(invocation -> {
			Comment savedComment = invocation.getArgument(0);
			ReflectionTestUtils.setField(savedComment, "id", commentId);
			return savedComment;
		});
	}

	@Test
	@DisplayName("댓글 저장 성공 : UserClient로 유저 정보를 조회하고 신규 댓글을 저장")
	void saveCommentTestSuccess() {
		// given
		Long feedId = 10L;
		Long userId = 9000L;
		Long commentId = 333L;
		String content = "테스트댓글";

		CommentCreateCommand commentCreateCommand = CommentCreateCommand.builder()
			.feedId(feedId)
			.userId(userId)
			.content(content)
			.build();

		UserGetResultDto mockUserResult = UserGetResultDto.builder()
			.id(userId)
			//.userName("tester")
			.profileImageUrl("/tester.png")
			.build();

		setupFeedClientMock(feedId);
		setupUserClientMock(userId);
		setupRepositorySaveMock(commentId);

		// when
		CommentCreateResponseDto commentCreateResponseDto = commentService.saveComment(commentCreateCommand);

		// then
		assertThat(commentCreateResponseDto.getCommentId()).isEqualTo(commentId);
		assertThat(commentCreateResponseDto.getContent()).isEqualTo(content);
		assertThat(commentCreateResponseDto.getCreatedBy()).isEqualTo(userId);

		verify(feedClient).getFeed(feedId);
		verify(userClient).getUser(userId);
		verify(commentRepository).save(any(Comment.class));
	}

	@Test
	@DisplayName("대댓글 저장 성공 : parentId가 있으면 원 댓글를 조회하고 도메인 서비스를 호출해서 검증")
	void saveReplyTestSuccess() {
		// given
		Long feedId = 100L;
		Long userId = 9874L;
		Long parentId = 50L;
		Long replyId = 2L;

		CommentCreateCommand command = CommentCreateCommand.builder()
			.feedId(feedId)
			.userId(userId)
			.content("대댓글")
			.parentId(parentId) // parentId 존재
			.build();

		setupFeedClientMock(feedId);
		setupUserClientMock(userId);
		setupRepositorySaveMock(replyId);

		Comment parentComment = Comment.builder().feedId(feedId).content("대댓글달댓글").build();
		given(commentRepository.findByIdAndDeletedAtIsNull(parentId))
			.willReturn(Optional.of(parentComment));

		willDoNothing().given(commentPolicy).validateReply(parentComment, feedId);

		// when
		CommentCreateResponseDto response = commentService.saveComment(command);

		// then
		assertThat(response.getCommentId()).isEqualTo(replyId);

		verify(commentRepository).findByIdAndDeletedAtIsNull(parentId);
		verify(commentPolicy).validateReply(parentComment, feedId);
	}

	@Test
	@DisplayName("대댓글 저장 실패 : 원 댓글이 없으면 예외 발생")
	void saveComment_Reply_Fail_ParentNotFound() {
		// given
		Long feedId = 100L;
		Long parentId = 999L;

		CommentCreateCommand command = CommentCreateCommand.builder()
			.feedId(feedId)
			.userId(1L)
			.content("대댓글")
			.parentId(parentId)
			.build();

		setupFeedClientMock(feedId);
		given(commentRepository.findByIdAndDeletedAtIsNull(parentId))
			.willReturn(Optional.empty());

		// when & then
		assertThatThrownBy(() -> commentService.saveComment(command))
			.hasMessage("존재하지 않거나 삭제된 댓글입니다.");

		verify(commentPolicy, times(0)).validateReply(any(), any());
	}

}
