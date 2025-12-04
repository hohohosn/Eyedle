package com.user_service.infrastructure.exception;

import lombok.extern.slf4j.Slf4j;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

	/**
	 * Validation 실패
	 */
	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ResponseEntity<ErrorResponse> handleValidationException(
		MethodArgumentNotValidException ex
	) {
		List<ErrorResponse.FieldError> errors = ex.getBindingResult()
			.getAllErrors()
			.stream()
			.map(error -> {
				FieldError fieldError = (FieldError)error;
				return ErrorResponse.FieldError.builder()
					.field(fieldError.getField())
					.value(String.valueOf(fieldError.getRejectedValue()))
					.reason(fieldError.getDefaultMessage())
					.build();
			})
			.collect(Collectors.toList());

		log.error("Validation 오류: {}", errors);

		ErrorResponse response = ErrorResponse.of(ErrorCode.INVALID_INPUT_VALUE, errors);

		return ResponseEntity
			.status(ErrorCode.INVALID_INPUT_VALUE.getStatus())
			.body(response);
	}

	/**
	 * BusinessException
	 */
	@ExceptionHandler(BusinessException.class)
	public ResponseEntity<ErrorResponse> handleBusinessException(BusinessException ex) {
		log.error("BusinessException: code={}, message={}",
			ex.getErrorCode().getCode(), ex.getMessage());

		ErrorResponse response = ErrorResponse.of(ex.getErrorCode());

		return ResponseEntity
			.status(ex.getErrorCode().getStatus())
			.body(response);
	}

	/**
	 * AccessDeniedException (403)
	 */
	@ExceptionHandler(AccessDeniedException.class)
	public ResponseEntity<ErrorResponse> handleAccessDeniedException(AccessDeniedException ex) {
		log.error("AccessDeniedException: {}", ex.getMessage());

		ErrorResponse response = ErrorResponse.of(ErrorCode.FORBIDDEN);

		return ResponseEntity
			.status(ErrorCode.FORBIDDEN.getStatus())
			.body(response);
	}

	/**
	 * 기타 예외
	 */
	@ExceptionHandler(Exception.class)
	public ResponseEntity<ErrorResponse> handleException(Exception ex) {
		log.error("서버 오류 발생: ", ex);

		ErrorResponse response = ErrorResponse.of(ErrorCode.INTERNAL_SERVER_ERROR);

		return ResponseEntity
			.status(ErrorCode.INTERNAL_SERVER_ERROR.getStatus())
			.body(response);
	}
}
