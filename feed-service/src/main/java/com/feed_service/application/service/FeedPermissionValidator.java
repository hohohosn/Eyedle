package com.feed_service.application.service;

import com.common.exception.CustomException;
import com.common.response.ErrorCode;
import com.feed_service.domain.model.Feed;
import com.feed_service.domain.model.FeedPermission;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class FeedPermissionValidator {

        private final FollowQueryService followQueryService;

        public void validationView(Long viewrId, Feed feed) {

                //작성자는 항상 조회 가능
                if(feed.getUserId().equals(viewrId)) {
                        return;
                }

                switch (feed.getPermission()) {
                        case PUBLIC -> {
                                return;
                        }
                        case FOLLOWERS -> {
                                boolean isFollowers = followQueryService
                                        .isFollower(feed.getUserId(), viewrId);
                                if(!isFollowers) {
                                        throw new CustomException(ErrorCode.FORBIDDEN);
                                }
                        }
                        case MUTUAL -> {
                                boolean isMutual = followQueryService
                                        .isMutual(feed.getUserId(), viewrId);
                                if(!isMutual) {
                                        throw new CustomException(ErrorCode.FORBIDDEN);
                                }
                        }
                        case PRIVATE -> {
                                throw new CustomException(ErrorCode.FORBIDDEN);
                        }
                }
        }

        public void ValidateModify(Long userId, Feed feed) {
                if(!feed.getUserId().equals(userId)) {
                        throw new CustomException(ErrorCode.FORBIDDEN);
                }
        }
}
