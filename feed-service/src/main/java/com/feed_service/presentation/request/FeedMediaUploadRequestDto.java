package com.feed_service.presentation.request;

import com.feed_service.domain.model.MediaType;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class FeedMediaUploadRequestDto {

    private String mediaUrl;
    private MediaType mediaType;

}
