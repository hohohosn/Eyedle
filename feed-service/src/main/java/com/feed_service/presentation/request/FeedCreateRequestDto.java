package com.feed_service.presentation.request;

import com.feed_service.domain.model.Feed;
import com.feed_service.domain.model.FeedMedia;
import com.feed_service.domain.model.FeedPermission;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;

import java.util.List;

@Getter
public class FeedCreateRequestDto {

    @NotNull
    private FeedPermission permission;

    @NotBlank
    private String content;

//    private List<FeedMediaUploadRequestDto> medias;

    private List<String> tags;

}
