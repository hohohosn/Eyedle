package com.feed_service.presentation.response;

import com.feed_service.domain.model.FeedMedia;
import com.feed_service.domain.model.MediaType;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class FeedMediaResponseDto {
    private Long mediaId;
    private String mediaUrl;
    private MediaType mediaType;

    public static FeedMediaResponseDto from(FeedMedia fm) {
        return new FeedMediaResponseDto(
                fm.getId(),
                fm.getMediaUrl(),
                fm.getMediaType()
        );
    }
}
