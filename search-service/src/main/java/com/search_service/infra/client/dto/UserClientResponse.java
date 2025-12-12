package com.search_service.infra.client.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserClientResponse {

	private Long id;

	private String email;

	private String username;

	private String profileImageUrl;

	private String status;
}
