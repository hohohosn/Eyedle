package com.search_service.presentation.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.search_service.application.service.SearchService;
import com.search_service.presentation.response.SearchResponse;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/search")
@RequiredArgsConstructor
public class SearchController {

	private final SearchService searchService;

	@GetMapping("/init")
	public String initData(){
		searchService.createMockData();
		return "Mock Data Inserted";
	}

	@GetMapping
	public SearchResponse search(@RequestParam("keyword") String keyword){
		return searchService.search(keyword);
	}

	@GetMapping("/clear")
	public String clear(){
		searchService.clearAll();
		return "All cleared";
	}
}
