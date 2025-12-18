package com.feed_service.presentation.controller;

import com.common.response.CommonResponse;
import com.common.response.ErrorCode;
import com.common.response.SuccessCode;
import com.feed_service.application.service.FeedLikeService;
import com.feed_service.domain.model.FeedBookmark;
import com.feed_service.domain.model.FeedLike;
import com.feed_service.presentation.response.FeedLikeResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/feeds/{feedId}/likes")
public class FeedLikeController {

    private final FeedLikeService feedLikeService;

    @PostMapping
    public CommonResponse toggleLike(@PathVariable Long feedId, @RequestHeader("X-User-Id") Long userId){

        FeedLikeResponseDto responseDto = feedLikeService.toggleLike(feedId, userId);

        return CommonResponse.of(SuccessCode.OK, responseDto);
    }
}
