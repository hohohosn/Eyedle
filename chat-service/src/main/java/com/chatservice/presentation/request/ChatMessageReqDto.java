package com.chatservice.presentation.request;

public record ChatMessageReqDto(
    String message,
    String senderEmail
) {

}
