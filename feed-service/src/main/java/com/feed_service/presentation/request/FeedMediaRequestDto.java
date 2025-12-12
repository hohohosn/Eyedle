package com.feed_service.presentation.request;

import com.feed_service.domain.model.FeedMedia;
import com.feed_service.domain.model.MediaType;
import lombok.Getter;

@Getter
public class FeedMediaRequestDto {
    private final String mediaUrl;
    private final MediaType mediaType;

    public FeedMediaRequestDto(FeedMedia media) {
        this.mediaUrl = media.getMediaUrl();
        this.mediaType = media.getMediaType();
    }
}
