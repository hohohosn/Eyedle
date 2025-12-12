package com.search_service.infra.client;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import com.search_service.infra.client.dto.FeedClientResponse;

//@FeignClient(name = "feed-service", path = "/feeds")
@FeignClient(name = "feed-service", url = "http://localhost:19600", path = "/mock/feeds")
public interface FeedFeignClient {

	@GetMapping
	List<FeedClientResponse> getFeeds();

	@GetMapping("/{feedId}")
	FeedClientResponse getFeedById(@PathVariable("feedId") Long feedId);

	//@GetMapping("/search/recent")
	@GetMapping("/recent")
	List<FeedClientResponse> searchRecentFeeds(
		@RequestParam("keyword") String keyword,
		@RequestParam("since")LocalDateTime since
	);
}
