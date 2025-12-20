package com.feed_service.infra.user.service;

import com.common.exception.CustomException;
import com.common.response.ErrorCode;
import com.feed_service.infra.user.UserClient;
import com.feed_service.infra.user.dto.UserInfoResponseDto;
import feign.FeignException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserQueryService {

    private final UserClient userClient;

    public UserInfoResponseDto loadUser(Long userId) {
        return userClient.getUser(userId);
    }

    public Map<Long, UserInfoResponseDto> loadUsers(Set<Long> userIds) {
        return userClient.getUsersByIds(userIds).stream()
                .collect(Collectors.toMap(
                        UserInfoResponseDto::getId,
                        u -> u
                ));
    }
}