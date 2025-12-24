package com.feed_service.application.service;

import com.feed_service.infra.user.client.UserServiceFeignClient;
import com.feed_service.infra.user.dto.UserInternalResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class FollowQueryService {

    private final UserServiceFeignClient userServiceFeignClient;

    public List<Long> getFollowers(Long userId){

        return userServiceFeignClient.getFollowers(userId)
                .stream()
                .map(UserInternalResponseDto::getUserId)
                .toList();
    }

    public boolean isFollower(Long targetUserId, Long viewerId) {

        List<UserInternalResponseDto> followers = userServiceFeignClient.getFollowers(targetUserId);

        return followers.stream()
                .anyMatch(u -> u.getUserId().equals(viewerId));
    }

    public boolean isMutual(Long userId1, Long userId2) {

        boolean user1FollowerUser2 = userServiceFeignClient.getFollowers(userId2)
                .stream()
                .anyMatch(u -> u.getUserId().equals(userId1));

        if (!user1FollowerUser2) return false;

        boolean user2FollowerUser1 = userServiceFeignClient
                .getFollowers(userId1)
                .stream()
                .anyMatch(u -> u.getUserId().equals(userId2));

        return user2FollowerUser1;
    }
}
