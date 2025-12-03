package com.feed_service.presentation.request;

import com.feed_service.domain.model.FeedPermission;
import lombok.Getter;

@Getter
public class FeedUpdateRequestDto {

    private String content;
    private FeedPermission permission;

}
