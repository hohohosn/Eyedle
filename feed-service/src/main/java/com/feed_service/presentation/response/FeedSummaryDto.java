package com.feed_service.presentation.response;

import com.feed_service.domain.model.Feed;
import com.feed_service.domain.model.FeedMedia;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class FeedSummaryDto {

    private Long id;
    private Long userId;
    private String content;
    private List<String> tags;
    private List<String> mediaUrls;

    private boolean liked;
    private boolean bookmarked;

    public static FeedSummaryDto of(
            Feed feed,
            List<String> tags,
            List<String> mediaUrls,
            boolean liked,
            boolean bookmarked
    ) {
        return new FeedSummaryDto(
                feed.getId(),
                feed.getUserId(),
                feed.getContent(),
                tags,
                mediaUrls,
                liked,
                bookmarked
        );
    }
}
