package com.feed_service.infra.user.service;

import com.common.exception.CustomException;
import com.common.response.ErrorCode;
import com.feed_service.infra.user.UserClient;
import com.feed_service.infra.user.dto.UserInfoResponseDto;
import feign.FeignException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserQueryService {

    private final UserClient userClient;

    public UserInfoResponseDto loadUser(Long userId) {
        try {
            return userClient.getUser(userId);
        } catch (FeignException.NotFound e) {
            throw new CustomException(ErrorCode.NOT_FOUND);
        }
    }
}
