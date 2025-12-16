package com.feed_service.presentation.controller;

import com.common.response.CommonResponse;
import com.common.response.SuccessCode;
import com.feed_service.application.service.FeedRecentService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;

@RestController
@RequiredArgsConstructor
@RequestMapping("/feeds")
public class FeedRecentController {

    private final FeedRecentService feedRecentService;

    @GetMapping("/recent")
    public CommonResponse getRecentFeed(@RequestParam LocalDateTime since,
                                        @RequestParam(required = false) String keyword) {

        return CommonResponse.of(SuccessCode.OK, feedRecentService.findRecentFeeds(since, keyword));
    }
}
