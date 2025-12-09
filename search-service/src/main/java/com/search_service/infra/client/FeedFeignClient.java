package com.search_service.infra.client;

import java.util.List;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import com.search_service.infra.client.dto.FeedClientResponse;

@FeignClient(name = "feed-service", path = "/feeds")
public interface FeedFeignClient {

	@GetMapping
	List<FeedClientResponse> getFeeds();

	@GetMapping("/{feedId}")
	FeedClientResponse getFeedById(@PathVariable("feedId") Long feedId);
}
