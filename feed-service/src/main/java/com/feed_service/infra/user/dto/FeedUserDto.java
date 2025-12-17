package com.feed_service.infra.user.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class FeedUserDto {
    private Long id;
    private String username;
    private String email;
}
