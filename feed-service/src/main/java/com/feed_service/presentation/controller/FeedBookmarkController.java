package com.feed_service.presentation.controller;

import com.common.response.CommonResponse;
import com.common.response.SuccessCode;
import com.feed_service.application.service.FeedBookmarkService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/feeds/{feedId}/bookmarks")
public class FeedBookmarkController {

    private final FeedBookmarkService feedBookmarkService;

    @PostMapping
    public CommonResponse bookmark(@PathVariable Long feedId) {
        Long userId = 1L;
        feedBookmarkService.bookmarkFeed(feedId, userId);
        return CommonResponse.of(SuccessCode.CREATED);
    }

    @DeleteMapping
    public CommonResponse unbookmark(@PathVariable Long feedId) {
        Long userId = 1L;
        feedBookmarkService.deleteBookmarkFeed(feedId, userId);
        return CommonResponse.of(SuccessCode.DELETED);
    }
}
