package com.feed_service.presentation.controller;

import com.common.response.CommonResponse;
import com.common.response.SuccessCode;
import com.feed_service.application.service.FeedService;
import com.feed_service.presentation.request.FeedCreateRequestDto;
import com.feed_service.presentation.response.FeedResponseDto;
import com.feed_service.presentation.response.TimelineResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.List;


@RestController
@RequiredArgsConstructor
@RequestMapping("/feeds")
public class FeedController {

    private final FeedService feedService;

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public CommonResponse<Long> createFeed(
            @RequestPart("request") FeedCreateRequestDto request,
            @RequestPart(value = "medias", required = false) List<MultipartFile> medias,
            @RequestHeader("X-User-Id") Long userId
    ) {
        Long id = feedService.createFeed(request, userId, medias);
        return CommonResponse.of(SuccessCode.OK, id);
    }
    @GetMapping("/main/timeline")
    public CommonResponse<TimelineResponseDto> getTimeline(
            @RequestHeader("X-User-Id") Long userId,
            @RequestParam(required = false) LocalDateTime cursorCreatedAt,
            @RequestParam(required = false) Long cursorId,
            @RequestParam(defaultValue = "10") int size
    ) {

        return CommonResponse.of(SuccessCode.OK, feedService.getTimeline(userId, cursorCreatedAt, cursorId, size));
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
            @RequestPart("request") FeedCreateRequestDto request,
            @RequestHeader("X-User-Id") Long userId,
            List<MultipartFile> images
    ) {
        return CommonResponse.of(
                SuccessCode.OK,
                feedService.updateFeed(feedId, request, userId, images)
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
