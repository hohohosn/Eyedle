package com.feed_service.presentation.controller;

import com.common.response.CommonResponse;
import com.common.response.SuccessCode;
import com.feed_service.application.service.TagQueryService;
import com.feed_service.domain.model.Tag;
import com.feed_service.domain.repository.TagRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

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