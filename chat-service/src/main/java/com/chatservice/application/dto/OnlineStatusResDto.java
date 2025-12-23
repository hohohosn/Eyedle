package com.chatservice.application.dto;

import com.chatservice.domain.model.OnlineStatus;

public record OnlineStatusResDto(
    Long userId,
    OnlineStatus onlineStatus,
    Long lastSeen
) {

}
