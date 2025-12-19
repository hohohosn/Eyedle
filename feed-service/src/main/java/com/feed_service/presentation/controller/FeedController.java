package com.feed_service.presentation.controller;

import com.common.response.CommonResponse;
import com.common.response.SuccessCode;
import com.feed_service.application.service.FeedService;
import com.feed_service.presentation.request.FeedCreateRequestDto;
import com.feed_service.presentation.request.FeedUpdateRequestDto;
import com.feed_service.presentation.response.FeedResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;




@RestController
@RequiredArgsConstructor
@RequestMapping("/feeds")
public class FeedController {

    private final FeedService feedService;

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public CommonResponse<Long> createFeed(
            @RequestPart("request") FeedCreateRequestDto request,
            @RequestHeader("X-User-Id") Long userId
    ) {
        Long id = feedService.createFeed(request, userId);
        return CommonResponse.of(SuccessCode.OK, id);
    }

    @GetMapping("/{feedId}")
    public CommonResponse<FeedResponseDto> getFeed(
            @PathVariable Long feedId,
            @RequestHeader("X-User-Id") Long userId
    ) {
        return CommonResponse.of(SuccessCode.OK, feedService.findFeed(feedId, userId));
    }

    @GetMapping
    public CommonResponse<Page<FeedResponseDto>> getFeeds(
            @PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.DESC)
            Pageable pageable,
            @RequestHeader("X-User-Id") Long userId
    ) {
        return CommonResponse.of(SuccessCode.OK, feedService.findAllFeeds(pageable, userId));
    }

    @PatchMapping("/{feedId}")
    public CommonResponse<FeedResponseDto> updateFeed(
            @PathVariable Long feedId,
            @RequestBody FeedUpdateRequestDto request,
            @RequestHeader("X-User-Id") Long userId
    ) {
        return CommonResponse.of(
                SuccessCode.OK,
                feedService.updateFeed(feedId, request, userId)
        );
    }

    @DeleteMapping("/{feedId}")
    public CommonResponse<Void> deleteFeed(
            @PathVariable Long feedId,
            @RequestHeader("X-User-Id") Long userId
    ) {
        feedService.deleteFeed(feedId, userId);
        return CommonResponse.of(SuccessCode.DELETED);
    }
}
