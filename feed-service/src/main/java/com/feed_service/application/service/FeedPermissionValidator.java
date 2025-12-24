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

        //예외를 던지지 않고 "조회 가능 여부"만 판단 (리스트/타임라인용)
        public boolean canView(Long viewerId, Feed feed) {

                // 작성자는 항상 조회 가능
                if (feed.getUserId().equals(viewerId)) {
                        return true;
                }

                return switch (feed.getPermission()) {
                        case PUBLIC -> true;
                        case PRIVATE -> false;
                        case FOLLOWERS ->
                                followQueryService.isFollower(feed.getUserId(), viewerId);
                        case MUTUAL ->
                                followQueryService.isMutual(feed.getUserId(), viewerId);
                        case CLOSE_FRIENDS ->
                                false; // 지금은 정책 미정 → false OK
                };
        }

        //단건 조회용 (권한 없으면 예외)
        public void validateView(Long viewrId, Feed feed) {

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

        //수정/삭제 권한
        public void validateModify(Long userId, Feed feed) {
                if(!feed.getUserId().equals(userId)) {
                        throw new CustomException(ErrorCode.FORBIDDEN);
                }
        }
}
