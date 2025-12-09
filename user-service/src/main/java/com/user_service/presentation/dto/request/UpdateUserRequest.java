package com.user_service.presentation.dto.request;

import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.*;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UpdateUserRequest {

	@Size(min = 3, max = 30, message = "사용자명은 3자 이상 30자 이하여야 합니다.")
	@Pattern(regexp = "^[a-zA-Z0-9_]+$", message = "사용자명은 영문, 숫자, 언더스코어만 사용 가능합니다.")
	private String username;

	@Size(min = 6, max = 100, message = "비밀번호는 6자 이상이어야 합니다.")
	private String currentPassword;

	@Size(min = 6, max = 100, message = "비밀번호는 6자 이상이어야 합니다.")
	private String newPassword;
}