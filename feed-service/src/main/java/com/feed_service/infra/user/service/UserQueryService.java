package com.feed_service.infra.user.service;

import com.common.exception.CustomException;
import com.common.response.ErrorCode;
import com.feed_service.infra.user.UserClient;
import com.feed_service.infra.user.response.ApiResponseDto;
import com.feed_service.infra.user.response.UserInfoResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserQueryService {

    private final UserClient userClient;

    public UserInfoResponseDto loadUser(Long userId) {
        ApiResponseDto<UserInfoResponseDto> responseDto = userClient.getUserInfo(userId);

        if(!responseDto.isSuccess() || responseDto.getData() == null){
            throw new CustomException(ErrorCode.NOT_FOUND);
        }

        return responseDto.getData();
    }
}
