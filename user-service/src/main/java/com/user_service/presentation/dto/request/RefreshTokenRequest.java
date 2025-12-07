package com.user_service.presentation.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class RefreshTokenRequest {

	@NotBlank(message = "Access Token은 필수입니다.")
	private String accessToken;

	@NotBlank(message = "Refresh Token은 필수입니다.")
	private String refreshToken;
}