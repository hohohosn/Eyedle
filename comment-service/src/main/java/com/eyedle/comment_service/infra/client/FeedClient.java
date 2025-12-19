package com.eyedle.comment_service.infra.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import com.common.response.CommonResponse;
import com.eyedle.comment_service.infra.client.dto.FeedGetResultDto;
import com.eyedle.comment_service.infra.config.FeignConfig;

@FeignClient(name = "feed-service", url = "http://feed-service-790073708.ap-northeast-2.elb.amazonaws.com", configuration = FeignConfig.class)
public interface FeedClient {

	@GetMapping("/feeds/{feedId}")
	CommonResponse<FeedGetResultDto> getFeed(@PathVariable("feedId") Long feedId);
}
