package com.feed_service.presentation.controller;

import com.common.response.CommonResponse;
import com.common.response.SuccessCode;
import com.feed_service.application.service.FeedService;
import com.feed_service.presentation.request.FeedCreateRequestDto;
import com.feed_service.presentation.request.FeedUpdateRequestDto;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
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

    @GetMapping
    public CommonResponse getFeeds(
            @PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.DESC)
            Pageable pageable
    ) {
        return CommonResponse.of(SuccessCode.OK, feedService.findAllFeeds(pageable));
    }

    @PatchMapping("/{feedId}")
    public CommonResponse  updateFeed(@PathVariable Long feedId, @RequestBody FeedUpdateRequestDto request){
        return CommonResponse.of(SuccessCode.OK, feedService.updateFeed(feedId, request));
    }

    @DeleteMapping("/{feedId}")
    public CommonResponse deleteFeed(@PathVariable Long feedId){
        feedService.statusDeleted(feedId);
        return CommonResponse.of(SuccessCode.OK);
    }
}
