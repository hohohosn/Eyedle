package com.feed_service.presentation.request;

import com.feed_service.domain.model.FeedPermission;
import lombok.Getter;

import java.util.List;

@Getter
public class FeedUpdateRequestDto {

    private String content;

    private FeedPermission permission;

    private List<FeedMediaUploadRequestDto> medias;

    private List<String> tags;

}
