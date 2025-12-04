package com.eyedle.comment_service.infra.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import com.common.response.CommonResponse;
import com.eyedle.comment_service.infra.client.dto.FeedGetResultDto;

@FeignClient(name = "feed-service")
public interface FeedClient {

	@GetMapping("/feeds/{feedId}")
	CommonResponse<FeedGetResultDto> getFeed(@PathVariable("feedId") Long feedId);
}
