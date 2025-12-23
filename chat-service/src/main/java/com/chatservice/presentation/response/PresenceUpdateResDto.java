package com.chatservice.presentation.response;

public record PresenceUpdateResDto(
    Long userId,
    String status
) {}
