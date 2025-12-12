package com.search_service.presentation.controller;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.context.annotation.Profile;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.search_service.infra.client.dto.FeedClientResponse;
import com.search_service.infra.client.dto.UserClientResponse;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
//@Profile("local")
public class MockDataController {
	@GetMapping("/mock/user") // 전체 조회
	public List<UserClientResponse> getAllUsers() {
		log.info("[Mock] User Service 전체 데이터 요청 받음");
		return List.of(
			UserClientResponse.builder().id(1L).username("test_user_1").profileImageUrl("url1.jpg").status("ACTIVE").build(),
			UserClientResponse.builder().id(2L).username("jeju_lover").profileImageUrl("url2.jpg").status("ACTIVE").build()
		);
	}

	@GetMapping("/mock/user/recent") // 최근 변경 조회
	public List<UserClientResponse> searchRecentUsers(
		@RequestParam(required = false) String keyword,
		@RequestParam LocalDateTime since) {
		log.info("[Mock] User Service 실시간 조회 요청 (keyword={}, since={})", keyword, since);

		return List.of(
			UserClientResponse.builder().id(999L).username("new_live_user").profileImageUrl("live.jpg").status("ACTIVE").build()
		);
	}

	@GetMapping("/mock/user/{userId}")
	public UserClientResponse getUserById(@PathVariable Long userId) {
		return UserClientResponse.builder().id(userId).username("mock_user_" + userId).build();
	}

	@GetMapping("/mock/feeds") // 전체 조회
	public List<FeedClientResponse> getFeeds() {
		log.info("[Mock] Feed Service 전체 데이터 요청 받음");
		return List.of(
			FeedClientResponse.builder()
				.feedId(10L).userId(1L).content("제주도 여행 너무 좋다").tags(List.of("제주", "여행"))
				.createdAt(LocalDateTime.now().minusHours(1))
				.isDeleted(false).likeCount(5)
				.medias(List.of(new FeedClientResponse.FeedMediaDto(1L, "IMAGE", "url", "thumb.jpg")))
				.build(),
			FeedClientResponse.builder()
				.feedId(20L).userId(2L).content("맛집 탐방").tags(List.of("맛집"))
				.createdAt(LocalDateTime.now().minusHours(2))
				.isDeleted(false).likeCount(10)
				.build()
		);
	}

	@GetMapping("/mock/feeds/recent") // 최근 변경 조회
	public List<FeedClientResponse> searchRecentFeeds(
		@RequestParam(required = false) String keyword,
		@RequestParam LocalDateTime since) {
		log.info("[Mock] Feed Service 실시간 조회 요청 (keyword={}, since={})", keyword, since);

		return List.of(
			FeedClientResponse.builder()
				.feedId(777L).userId(999L).content("방금 올린 따끈따끈한 제주도 사진").tags(List.of("제주", "실시간"))
				.createdAt(LocalDateTime.now())
				.isDeleted(false).likeCount(0)
				.medias(List.of(new FeedClientResponse.FeedMediaDto(2L, "IMAGE", "url", "live_thumb.jpg")))
				.build()
		);
	}
}
