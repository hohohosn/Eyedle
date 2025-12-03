package com.feed_service.Common;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CommonResponse<T> {

    private boolean success;
    private T data;
    private String message;
    private String code;

    private CommonResponse(boolean success, T data, String message, String code) {
        this.success = success;
        this.data = data;
        this.message = message;
        this.code = code;
    }

    public static <T> CommonResponse<T> of(SuccessCode successCode, T data) {
        return new CommonResponse<>(
                true,
                data,
                successCode.getMessage(),
                successCode.getCode()
        );
    }

    public static <T> CommonResponse<T> of(SuccessCode successCode) {
        return new CommonResponse<>(
                true,
                null,
                successCode.getMessage(),
                successCode.getCode()
        );
    }

    public static <T> CommonResponse<T> of(ErrorCode errorCode) {
        return new CommonResponse<>(
                false,
                null,
                errorCode.getMessage(),
                errorCode.getCode()
        );
    }
}
