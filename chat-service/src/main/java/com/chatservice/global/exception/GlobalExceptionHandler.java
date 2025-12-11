package com.chatservice.global.exception;

import com.common.exception.CustomException;
import com.common.response.BaseErrorCode;
import com.common.response.CommonResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

  @ExceptionHandler(CustomException.class)
  public ResponseEntity<CommonResponse<Void>> handleCustomException(CustomException e) {
    BaseErrorCode errorCode = e.getErrorCode();

    return ResponseEntity
        .status(errorCode.getStatus())
        .body(CommonResponse.of(errorCode));
  }
}