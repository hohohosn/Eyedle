package com.feed_service.infra.user.response;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ApiResponseDto<T> {
    private boolean success;
    private String message;
    private T data;
}
