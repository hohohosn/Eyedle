package com.feed_service.presentation.controller;

import com.feed_service.Common.CommonResponse;
import com.feed_service.Common.SuccessCode;
import com.feed_service.application.service.FeedService;
import com.feed_service.presentation.request.FeedCreateRequestDto;
import com.feed_service.presentation.request.FeedUpdateRequestDto;
import com.sun.net.httpserver.Authenticator;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/feeds")
public class FeedController {

    private final FeedService feedService;

    @PostMapping
    public CommonResponse createFeed(@RequestBody FeedCreateRequestDto request){
        Long userId = 1L;
        Long feedId = 1L;

        return CommonResponse.of(SuccessCode.OK,
                feedService.createFeed(request, userId, feedId));
    }

    @GetMapping("/{feedId}")
    public CommonResponse getFeed(@PathVariable Long feedId){
        return CommonResponse.of(SuccessCode.OK, feedService.findFeed(feedId));
    }

    @PatchMapping("/{feedId}")
    public CommonResponse  updateFeed(@PathVariable Long feedId, @RequestBody FeedUpdateRequestDto request){
        return CommonResponse.of(SuccessCode.OK, feedService.updateFeed(feedId, request));
    }
}
