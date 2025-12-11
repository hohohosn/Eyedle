package com.feed_service.presentation.controller;

import com.common.response.CommonResponse;
import com.common.response.SuccessCode;
import com.feed_service.application.service.FeedLikeQueryService;
import com.feed_service.presentation.response.FeedSummaryDto;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/feeds/likes")
public class FeedLikeQueryController {

    private final FeedLikeQueryService feedLikeQueryService;

    @GetMapping
    public CommonResponse<List<FeedSummaryDto>> getMyLikedFeeds() {
        Long userId = 1L;
        return CommonResponse.of(SuccessCode.OK, feedLikeQueryService.getUserLikedFeeds(userId));
    }
}
