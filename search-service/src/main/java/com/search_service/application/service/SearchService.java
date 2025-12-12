package com.search_service.application.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

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

	private final EsFeedSearchRepository esFeedRepository;
	private final EsUserSearchRepository esUserRepository;

	private final KeywordRepository keywordRepository;

	private final UserFeignClient userFeignClient;
	private final FeedFeignClient feedFeignClient;

	private LocalDateTime lastSyncTime = LocalDateTime.now().minusMinutes(10);

	//private final List<SseEmitter> emitters = new CopyOnWriteArrayList<>();

	public SearchResponse search(String keyword) {
		if (keyword == null || keyword.trim().isEmpty()) {
			throw new CustomException(ErrorCode.BAD_REQUEST);
		}

		try{
			keywordRepository.incrementScore(keyword);
		} catch (Exception e) {
			log.error("키워드 점수 증가 실패: {}", e.getMessage());
		}

		List<UserDocument> esUsers = esUserRepository.findByUsernameContaining(keyword);
		List<FeedDocument> esFeeds = esFeedRepository.findByContentContainingOrTagsContaining(keyword, keyword);

		LocalDateTime searchSince = (lastSyncTime != null) ? lastSyncTime : LocalDateTime.now().minusMinutes(10);

		List<UserDocument> liveUsers = getLiveUsers(keyword, searchSince);
		List<FeedDocument> liveFeeds = getLiveFeeds(keyword, searchSince);

		// 중복 제거
		List<UserDocument> mergedUsers = mergeUsers(esUsers, liveUsers);
		List<FeedDocument> mergedFeeds = mergeFeeds(esFeeds, liveFeeds);

		return SearchResponse.builder()
			.keyword(keyword)
			.result(SearchResponse.SearchResult.builder()
				.users(mergedUsers)
				.feeds(mergedFeeds)
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

	@Scheduled(fixedDelay = 3600000) // 1시간
	public void decayKeywordScores() {
		log.info("인기 검색어 점수 반감");
		try {
			keywordRepository.decayKeywordScores();
		} catch (Exception e) {
			log.error("인기 검색어 반감 처리 실패: {}", e.getMessage());
		}
	}

	@Scheduled(fixedDelay = 600000) // 10분
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

		esUserRepository.saveAll(userDocs);

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
					.userId(f.getUserId())
					.username(username)
					.userProfileUrl(profileUrl)
					.imageUrl(mainImageUrl)
					.likeCount(f.getLikeCount())
					.createdAt(f.getCreatedAt())
					.isDeleted(f.getIsDeleted())
					.build();
			})
			.toList();

		esFeedRepository.saveAll(feedDocs);

		this.lastSyncTime = LocalDateTime.now();
		log.info("Sync complete. Users: {}, Feeds: {}", userDocs.size(), feedDocs.size());
	}

	// public SseEmitter subscribe() {
	//
	// }

	private List<UserDocument> getLiveUsers(String keyword, LocalDateTime since) {
		try {
			List<UserClientResponse> responses = userFeignClient.searchRecentUsers(keyword, since);
			if (responses == null) return Collections.emptyList();

			return responses.stream()
				.map(this::convertToUserDocument)
				.toList();
		} catch (Exception e) {
			log.warn("User Service 실시간 조회 실패ㅣ {}", e.getMessage());
			return Collections.emptyList();
		}
	}

	private List<FeedDocument> getLiveFeeds(String keyword, LocalDateTime since) {
		try {
			List<FeedClientResponse> responses = feedFeignClient.searchRecentFeeds(keyword, since);
			if (responses == null) return Collections.emptyList();

			return responses.stream()
				.map(this::convertToFeedDocument)
				.toList();
		} catch (Exception e) {
			log.warn("Feed Service 실시간 조회 실패: {}", e.getMessage());
			return Collections.emptyList();
		}
	}

	private List<FeedDocument> mergeFeeds(List<FeedDocument> esList, List<FeedDocument> liveList) {
		Map<Long, FeedDocument> map = new HashMap<>();
		esList.forEach(f -> map.put(f.getId(), f));
		liveList.forEach(f -> map.put(f.getId(), f));

		return map.values().stream()
			.sorted(Comparator.comparing(FeedDocument::getCreatedAt).reversed())
			.toList();
	}

	private List<UserDocument> mergeUsers(List<UserDocument> esList, List<UserDocument> liveList) {
		Map<Long, UserDocument> map = new HashMap<>();
		esList.forEach(u -> map.put(u.getId(), u));
		liveList.forEach(u -> map.put(u.getId(), u));

		return new ArrayList<>(map.values());
	}

	private FeedDocument convertToFeedDocument(FeedClientResponse response) {
		String mainImageUrl = (response.getMedias() != null && !response.getMedias().isEmpty()) ? response.getMedias().get(0).getThumbnailUrl() : null;

		return FeedDocument.builder()
			.id(response.getFeedId())
			.content(response.getContent())
			.tags(response.getTags())
			.userId(response.getUserId())
			.username("Live User") // 리팩토링
			.imageUrl(mainImageUrl)
			.likeCount(response.getLikeCount())
			.createdAt(response.getCreatedAt())
			.isDeleted(response.getIsDeleted())
			.build();
	}

	private UserDocument convertToUserDocument(UserClientResponse responce) {
		return UserDocument.builder()
			.id(responce.getId())
			.username(responce.getUsername())
			.profileImageUrl(responce.getProfileImageUrl())
			.status(responce.getStatus())
			.build();
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
