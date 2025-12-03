package com.feed_service.Common;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {

    BAD_REQUEST("E400", "잘못된 요청입니다."),
    UNAUTHORIZED("E401", "인증 정보가 없습니다."),
    FORBIDDEN("E403", "접근 권한이 없습니다."),
    NOT_FOUND("E404", "대상을 찾을 수 없습니다."),
    CONFLICT("E409", "이미 존재하는 데이터입니다."),
    SERVER_ERROR("E500", "서버 내부 오류가 발생했습니다.");

    private final String code;
    private final String message;
}
