package com.search_service.presentation.response;

import java.util.List;

import javax.naming.directory.SearchResult;

import com.search_service.domain.model.FeedDocument;
import com.search_service.domain.model.UserDocument;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class SearchResponse {

	private String keyword;
	private SearchResult result;

	@Getter
	@Builder
	@AllArgsConstructor
	public static class SearchResult{
		private List<UserDocument> users;
		private List<FeedDocument> feeds;>
	}
}
