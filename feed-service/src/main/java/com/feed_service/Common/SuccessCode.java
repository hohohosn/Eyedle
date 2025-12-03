package com.feed_service.Common;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum SuccessCode {

    OK("S200", "요청이 성공했습니다."),
    CREATED("S201", "정상적으로 생성되었습니다."),
    UPDATED("S204", "정상적으로 수정되었습니다."),
    DELETED("S205", "정상적으로 삭제되었습니다.");

    private final String code;
    private final String message;
}
