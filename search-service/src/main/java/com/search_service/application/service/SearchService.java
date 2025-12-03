package com.search_service.application.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import com.search_service.domain.model.FeedDocument;
import com.search_service.domain.model.KeywordScore;
import com.search_service.domain.model.UserDocument;
import com.search_service.domain.repository.FeedRepository;
import com.search_service.domain.repository.KeywordRepository;
import com.search_service.domain.repository.UserRepository;
import com.search_service.presentation.response.SearchRankResponse;
import com.search_service.presentation.response.SearchResponse;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class SearchService {

	private final UserRepository userRepository;
	private final FeedRepository feedRepository;
	private final KeywordRepository keywordRepository;

	// 임시 데이터
	public void createMockData(){
		userRepository.save(UserDocument.builder().id(1L).userId("jeju_native").profileImageUrl("img1.jpg").build());
		userRepository.save(UserDocument.builder().id(2L).userId("seoul_lover").profileImageUrl("img2.jpg").build());
		userRepository.save(UserDocument.builder().id(3L).userId("ilove_jeju").profileImageUrl("img3.jpg").build());

		feedRepository.save(FeedDocument.builder().id(100L).content("제주도 푸른 밤").tags(List.of("제주", "여행")).authorUserId("jeju_native").imageUrl("feed1.jpg").build());
		feedRepository.save(FeedDocument.builder().id(101L).content("제주 여행").tags(List.of()).authorUserId("jeju_native").imageUrl("feed1.jpg").build());
		feedRepository.save(FeedDocument.builder().id(102L).content("재밌다").tags(List.of("제주")).authorUserId("seoul_lover").imageUrl("feed2.jpg").build());
		feedRepository.save(FeedDocument.builder().id(103L).content("부산 바캉스").tags(List.of("부산", "바다")).authorUserId("seoul_lover").imageUrl("feed2.jpg").build());
	}

	public SearchResponse search(String keyword) {
		keywordRepository.incrementScore(keyword);

		List<UserDocument> users = userRepository.searchByNickname(keyword);
		List<FeedDocument> feeds = feedRepository.searchByKeyword(keyword);

		return SearchResponse.builder()
			.keyword(keyword)
			.result(SearchResponse.SearchResult.builder()
				.users(users)
				.feeds(feeds)
				.build())
			.build();
	}

	public SearchRankResponse getTopKeywords(int limit) {
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

	public void clearAll(){
		userRepository.deleteAll();
		feedRepository.deleteAll();
	}
}
