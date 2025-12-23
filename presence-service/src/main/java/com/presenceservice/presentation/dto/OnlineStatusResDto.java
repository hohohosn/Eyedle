package com.presenceservice.presentation.dto;

import com.presenceservice.domain.model.OnlineStatus;

public record OnlineStatusResDto(
    Long userId,
    OnlineStatus onlineStatus,
    Long lastSeen
) {

  public static OnlineStatusResDto of(Long userId, OnlineStatus onlineStatus, Long lastSeen) {
    return new OnlineStatusResDto(
        userId,
        onlineStatus,
        lastSeen
    );
  }
}
