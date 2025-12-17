package com.search_service.presentation.controller;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import com.search_service.application.service.SearchService;
import com.search_service.presentation.response.SearchRankResponse;
import com.search_service.presentation.response.SearchResponse;

import com.common.response.CommonResponse;
import com.common.response.SuccessCode;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/search")
@RequiredArgsConstructor
public class SearchController {

	private final SearchService searchService;

	@PostMapping("/sync")
	public CommonResponse<String> syncData(){
		searchService.syncData();
		return CommonResponse.of(SuccessCode.OK, "Data Synchronized Successfully");
	}

	@GetMapping
	public CommonResponse<SearchResponse> search(@RequestParam("keyword") String keyword){
		SearchResponse result = searchService.search(keyword);
		return CommonResponse.of(SuccessCode.OK, result);
	}

	@GetMapping("/hot")
	public CommonResponse<SearchRankResponse> getHotKeywords(@RequestParam(value="limit", defaultValue = "10") int limit){

		SearchRankResponse result = searchService.getTopKeywords(limit);
		return CommonResponse.of(SuccessCode.OK, result);
	}

	@GetMapping(value = "/hot/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
	public SseEmitter streamHotKeywords() {
		return searchService.subscribe();
	}

	// @GetMapping("/init")
	// public CommonResponse<String> initData(){
	// 	searchService.createMockData();
	// 	return CommonResponse.of(SuccessCode.CREATED, "Mock Data Inserted");
	// }

	// @GetMapping("/clear")
	// public CommonResponse<String> clear(){
	// 	searchService.clearAll();
	// 	return CommonResponse.of(SuccessCode.DELETED, "All cleared");
	// }
}
