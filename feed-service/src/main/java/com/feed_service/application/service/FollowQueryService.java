package com.feed_service.application.service;

import com.feed_service.infra.user.FollowClient;
import com.user_service.application.service.FollowService;
import com.user_service.presentation.dto.response.UserInternalResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Service
@RequiredArgsConstructor
public class FollowQueryService {

    private final FollowClient followClient;

    public List<Long> getFollowers(Long userId){

        return followClient.getFollowers(userId)
                .stream()
                .map(UserInternalResponse::getId)
                .toList();
    }
}
