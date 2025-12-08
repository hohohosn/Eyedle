package com.feed_service.application.service;

import com.feed_service.domain.model.Feed;
import com.feed_service.domain.model.FeedPermission;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class FeedPermissionValidator {

        public boolean canView(Long viewId, Feed feed){

                if(feed.getPermission().equals(FeedPermission.PUBLIC)) return true;
                if(feed.getPermission().equals(FeedPermission.PRIVATE)) return true;

                return switch (feed.getPermission()){
                        case PRIVATE -> false;
                        case FOLLOWERS ->  false;
                        case MUTUAL ->  false;
                        default -> false;
                };
        }
}
