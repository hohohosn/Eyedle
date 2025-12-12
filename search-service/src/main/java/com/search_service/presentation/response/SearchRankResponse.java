package com.search_service.presentation.response;

import java.time.LocalDateTime;
import java.util.List;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class SearchRankResponse {

	private LocalDateTime requestTime;
	private List<RankItem> ranks;

	@Getter
	@Builder
	public static class RankItem {
		private int rank;
		private String keyword;
		private double score;
	}
}
