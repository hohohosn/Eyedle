package com.feed_service.presentation.controller;

import com.common.response.CommonResponse;
import com.common.response.SuccessCode;
import com.feed_service.application.service.FeedBookmarkService;
import com.feed_service.presentation.response.FeedBookmarkResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/feeds/{feedId}/bookmarks")
public class FeedBookmarkController {

    private final FeedBookmarkService feedBookmarkService;

    @PostMapping
    public CommonResponse toggleBookmark(@PathVariable Long feedId) {
        Long userId = 1L;
        FeedBookmarkResponseDto responseDto = feedBookmarkService.toggleBookmakr(userId, feedId);

        return CommonResponse.of(SuccessCode.OK, responseDto);
    }
}
