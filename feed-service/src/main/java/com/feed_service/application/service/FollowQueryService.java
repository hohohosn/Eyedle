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
}
