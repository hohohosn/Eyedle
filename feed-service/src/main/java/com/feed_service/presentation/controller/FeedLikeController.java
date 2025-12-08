package com.feed_service.presentation.controller;

import com.common.response.CommonResponse;
import com.common.response.SuccessCode;
import com.feed_service.application.service.FeedLikeService;
import com.feed_service.domain.model.FeedLike;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/feeds/{feedId}/likes")
public class FeedLikeController {

    private final FeedLikeService feedLikeService;

    @PostMapping
    public CommonResponse like(@PathVariable Long feedId) {
        Long userId = 1L;
        feedLikeService.createLike(feedId, userId);
        return CommonResponse.of(SuccessCode.CREATED);
    }

    @DeleteMapping
    public CommonResponse unlike(@PathVariable Long feedId) {
        Long userId = 1L;
        feedLikeService.deleteLike(feedId, userId);
        return CommonResponse.of(SuccessCode.DELETED);
    }
}
