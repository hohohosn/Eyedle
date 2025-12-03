package com.eyedle.comment_service;

import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

import com.eyedle.comment_service.domain.model.Comment;
import com.eyedle.comment_service.domain.repository.CommentRepository;
import com.eyedle.comment_service.domain.vo.Author;
import com.eyedle.comment_service.infra.config.JpaAuditConfig;
import com.eyedle.comment_service.infra.repository.impl.CommentRepositoryImpl;

@DataJpaTest
@Import({
	JpaAuditConfig.class
	, CommentRepositoryImpl.class
})
public class CommentRepositoryTest {

	@Autowired
	private CommentRepository commentRepository;

	@Test
	@DisplayName("댓글 생성시 테이블 저장")
	void saveCommentTestSuccess() {

		// given
		Author author = Author.builder()
			.id(1111L)
			.name("tester")
			.profileImgUrl("url")
			.build();

		Comment comment = Comment.builder()
			.feedId(7777L)
			.content("댓글")
			.author(author)
			.build();

		// when
		Comment savedComment = commentRepository.save(comment);

		// then
		assertThat(savedComment.getId()).isNotNull();
		System.out.println("TSID : "+savedComment.getId());

		assertThat(savedComment.getContent()).isEqualTo("댓글");
		assertThat(savedComment.getFeedId()).isEqualTo(7777L);
		assertThat(savedComment.getAuthor().getName()).isEqualTo("tester");
		assertThat(savedComment.getCreatedAt()).isNotNull();

	}
}
