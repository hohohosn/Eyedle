package com.chatservice.application.dto;

public record UserInfoResDto(
    Long id,
    String username,
    String email,
    String profileImageUrl
) {

}
