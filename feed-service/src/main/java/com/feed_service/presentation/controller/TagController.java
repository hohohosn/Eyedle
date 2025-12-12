package com.feed_service.presentation.controller;

import com.common.response.CommonResponse;
import com.common.response.SuccessCode;
import com.feed_service.application.service.TagQueryService;
import com.feed_service.domain.repository.TagRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/tags")
public class TagController {

    private final TagQueryService tagQueryService;

    @GetMapping("/popular")
    public CommonResponse getPopularTags() {
        return CommonResponse.of(SuccessCode.OK, tagQueryService.getPopularTags());
    }
}
