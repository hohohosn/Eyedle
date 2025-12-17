package com.feed_service.presentation.controller;

import com.common.response.CommonResponse;
import com.common.response.ErrorCode;
import com.common.response.SuccessCode;
import com.feed_service.application.service.FeedService;
import com.feed_service.presentation.request.FeedCreateRequestDto;
import com.feed_service.presentation.request.FeedUpdateRequestDto;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.MediaType;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/feeds")
public class FeedController {

    private final FeedService feedService;

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public CommonResponse createFeed(
            @RequestPart("request") FeedCreateRequestDto request,
            @RequestPart(value = "files", required = false) List<MultipartFile> files,
            @AuthenticationPrincipal Long userId
    ){

        Long id = feedService.createFeed(request, files, userId);
        return CommonResponse.of(SuccessCode.OK, id);
    }

    @GetMapping("/{feedId}")
    public CommonResponse getFeed(@PathVariable Long feedId, @AuthenticationPrincipal Long userId){

        return CommonResponse.of(SuccessCode.OK, feedService.findFeed(feedId, userId));
    }

    @GetMapping
    public CommonResponse getFeeds(
            @PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.DESC)
            Pageable pageable,
            @AuthenticationPrincipal Long userId
    ) {

        return CommonResponse.of(SuccessCode.OK, feedService.findAllFeeds(pageable, userId));
    }

    @PatchMapping("/{feedId}")
    public CommonResponse  updateFeed(@PathVariable Long feedId, @RequestBody FeedUpdateRequestDto request){
        return CommonResponse.of(SuccessCode.OK, feedService.updateFeed(feedId, request));
    }

    @DeleteMapping("/{feedId}")
    public CommonResponse deleteFeed(@PathVariable Long feedId){
        boolean result = feedService.statusDeleted(feedId);

        if(!result){
            return CommonResponse.of(ErrorCode.NOT_FOUND);
        }

        return CommonResponse.of(SuccessCode.DELETED);
    }
}
