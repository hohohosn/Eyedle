package com.user_service.presentation.dto.response;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ApiResponse<T> {

	@Builder.Default
	private boolean success = true;

	private String message;
	private T data;

	@Builder.Default
	private LocalDateTime timestamp = LocalDateTime.now();

	public static <T> ApiResponse<T> success(T data) {
		return ApiResponse.<T>builder()
			.success(true)
			.message("요청이 성공적으로 처리되었습니다.")
			.data(data)
			.build();
	}

	public static <T> ApiResponse<T> success(String message, T data) {
		return ApiResponse.<T>builder()
			.success(true)
			.message(message)
			.data(data)
			.build();
	}

	public static ApiResponse<Void> success(String message) {
		return ApiResponse.<Void>builder()
			.success(true)
			.message(message)
			.data(null)
			.build();
	}

	public static <T> ApiResponse<T> error(String message, T data) {
		return ApiResponse.<T>builder()
			.success(false)
			.message(message)
			.data(data)
			.build();
	}
}