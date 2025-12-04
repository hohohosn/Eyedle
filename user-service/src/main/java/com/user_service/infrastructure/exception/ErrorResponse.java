package com.user_service.infrastructure.exception;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ErrorResponse {

	private boolean success;
	private String code;
	private String message;
	private List<FieldError> errors;

	@Builder.Default
	private LocalDateTime timestamp = LocalDateTime.now();

	@Getter
	@NoArgsConstructor
	@AllArgsConstructor
	@Builder
	public static class FieldError {
		private String field;
		private String value;
		private String reason;
	}

	public static ErrorResponse of(ErrorCode errorCode) {
		return ErrorResponse.builder()
			.success(false)
			.code(errorCode.getCode())
			.message(errorCode.getMessage())
			.build();
	}

	public static ErrorResponse of(ErrorCode errorCode, List<FieldError> errors) {
		return ErrorResponse.builder()
			.success(false)
			.code(errorCode.getCode())
			.message(errorCode.getMessage())
			.errors(errors)
			.build();
	}
}