package com.search_service.application.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.common.exception.CustomException;
import com.common.response.ErrorCode;
import com.search_service.domain.model.FeedDocument;
import com.search_service.domain.model.KeywordScore;
import com.search_service.domain.model.UserDocument;
import com.search_service.domain.repository.KeywordRepository;
import com.search_service.infra.client.FeedFeignClient;
import com.search_service.infra.client.UserFeignClient;
import com.search_service.infra.client.dto.FeedClientResponse;
import com.search_service.infra.client.dto.UserClientResponse;
import com.search_service.infra.repository.elasticsearch.EsFeedSearchRepository;
import com.search_service.infra.repository.elasticsearch.EsUserSearchRepository;
import com.search_service.presentation.response.SearchRankResponse;
import com.search_service.presentation.response.SearchResponse;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class SearchService {

	private final EsFeedSearchRepository feedRepository;
	private final EsUserSearchRepository userRepository;

	private final KeywordRepository keywordRepository;

	private final UserFeignClient userFeignClient;
	private final FeedFeignClient feedFeignClient;

	public SearchResponse search(String keyword) {
		if (keyword == null || keyword.trim().isEmpty()) {
			throw new CustomException(ErrorCode.BAD_REQUEST);
		}

		try{
			keywordRepository.incrementScore(keyword);
		} catch (Exception e) {
			log.error("키워드 점수 증가 실패: {}", e.getMessage());
		}

		List<UserDocument> users = userRepository.findByUsernameContaining(keyword);
		List<FeedDocument> feeds = feedRepository.findByContentContainingOrTagsContaining(keyword, keyword);

		return SearchResponse.builder()
			.keyword(keyword)
			.result(SearchResponse.SearchResult.builder()
				.users(users)
				.feeds(feeds)
				.build())
			.build();
	}

	public SearchRankResponse getTopKeywords(int limit) {
		if (limit <= 0) {
			throw new CustomException(ErrorCode.BAD_REQUEST);
		}

		List<KeywordScore> topKeywords = keywordRepository.getTopKeywords(limit);
		List<SearchRankResponse.RankItem> rankItems = new ArrayList<>();

		for(int i=0; i<topKeywords.size(); i++){
			KeywordScore item = topKeywords.get(i);
			rankItems.add(SearchRankResponse.RankItem.builder()
				.rank(i+1) // 1등부터
				.keyword(item.getKeyword())
				.score(item.getScore())
				.build());
		}

		return SearchRankResponse.builder()
			.requestTime(LocalDateTime.now())
			.ranks(rankItems)
			.build();
	}

	@Transactional
	@Scheduled(fixedDelay = 600000)
	public void syncData() {
		log.info("Start data synchronization...");

		List<UserClientResponse> users = userFeignClient.getAllUsers();
		if(users == null) users = Collections.emptyList();

		List<UserDocument> userDocs = users.stream()
			.map(u -> UserDocument.builder()
				.id(u.getId())
				.username(u.getUsername())
				.profileImageUrl(u.getProfileImageUrl())
				.status(u.getStatus())
				.build())
			.toList();

		userRepository.saveAll(userDocs);

		Map<Long, UserClientResponse> userMap = users.stream()
			.collect(Collectors.toMap(UserClientResponse::getId, Function.identity(), (p1, p2) -> p1));

		List<FeedClientResponse> feeds = feedFeignClient.getFeeds();
		if (feeds == null) feeds = Collections.emptyList();

		List<FeedDocument> feedDocs = feeds.stream()
			.map(f -> {
				// 작성자 정보 매핑
				UserClientResponse author = userMap.get(f.getUserId());
				String username = (author != null) ? author.getUsername() : "Unknown";
				String profileUrl = (author != null) ? author.getProfileImageUrl() : "";

				// 썸네일 추출 (첫 번째 미디어)
				String mainImageUrl = (f.getMedias() != null && !f.getMedias().isEmpty())
					? f.getMedias().get(0).getThumbnailUrl()
					: null;

				return FeedDocument.builder()
					.id(f.getFeedId())
					.content(f.getContent())
					.tags(f.getTags())
					.userId(f.getUserId()) // Long 타입
					.username(username)
					.userProfileUrl(profileUrl)
					.imageUrl(mainImageUrl)
					.likeCount(f.getLikeCount())
					.createdAt(f.getCreatedAt())
					.isDeleted(f.getIsDeleted())
					.build();
			})
			.toList();

		feedRepository.saveAll(feedDocs);

		log.info("Sync complete. Users: {}, Feeds: {}", userDocs.size(), feedDocs.size());
	}

	// private final UserRepository userRepository;
	// private final FeedRepository feedRepository;
	//
	// // 임시 데이터 하드코딩
	// // 추후 리팩토링 예정
	// public void createMockData(){
	// 	userRepository.save(UserDocument.builder().id(1L).userId("jeju_native").profileImageUrl("img1.jpg").build());
	// 	userRepository.save(UserDocument.builder().id(2L).userId("seoul_lover").profileImageUrl("img2.jpg").build());
	// 	userRepository.save(UserDocument.builder().id(3L).userId("ilove_jeju").profileImageUrl("img3.jpg").build());
	//
	// 	feedRepository.save(FeedDocument.builder().id(100L).content("제주도 푸른 밤").tags(List.of("제주", "여행")).userId("jeju_native").imageUrl("feed1.jpg").build());
	// 	feedRepository.save(FeedDocument.builder().id(101L).content("제주 여행").tags(List.of()).userId("jeju_native").imageUrl("feed1.jpg").build());
	// 	feedRepository.save(FeedDocument.builder().id(102L).content("재밌다").tags(List.of("제주")).userId("seoul_lover").imageUrl("feed2.jpg").build());
	// 	feedRepository.save(FeedDocument.builder().id(103L).content("부산 바캉스").tags(List.of("부산", "바다")).userId("seoul_lover").imageUrl("feed2.jpg").build());
	// }
	//
	// public void clearAll(){
	// 	userRepository.deleteAll();
	// 	feedRepository.deleteAll();
	// }
}
