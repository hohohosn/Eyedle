package com.feed_service.infra.user.response;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class UserInfoResponseDto {
    private Long id;
    private String email;
    private String username;
    private String slackId;
    private String role;
    private String status;
    private Boolean deleted;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
