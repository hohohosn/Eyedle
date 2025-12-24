package com.feed_service.infra.user.service;

import com.feed_service.infra.user.client.UserServiceFeignClient;
import com.feed_service.infra.user.dto.UserInfoResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserQueryService {

    private final UserServiceFeignClient userServiceFeignClient;

    public UserInfoResponseDto loadUser(Long userId) {
        return userServiceFeignClient.getUser(userId);
    }

    public Map<Long, UserInfoResponseDto> loadUsers(Set<Long> userIds) {
        return userServiceFeignClient.getUsersByIds(userIds).stream()
                .collect(Collectors.toMap(
                        UserInfoResponseDto::getId,
                        u -> u
                ));
    }
}