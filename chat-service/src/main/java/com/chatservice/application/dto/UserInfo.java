package com.chatservice.application.dto;

public record UserInfo(
    Long receiverId,
    String receiverUserName
) {

  public static UserInfo of(UserInfoResDto userInfoResDto) {
    return new UserInfo(
        userInfoResDto.id(),
        userInfoResDto.username()
    );
  }
}
