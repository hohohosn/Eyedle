package com.feed_service.application.service;

import com.feed_service.infra.user.FollowClient;
import com.feed_service.infra.user.dto.UserInternalResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class FollowQueryService {

    private final FollowClient followClient;

    public List<Long> getFollowers(Long userId){

        return followClient.getFollowers(userId)
                .stream()
                .map(UserInternalResponseDto::getUserId)
                .toList();
    }
}
