package com.chatservice.presentation.request;

import com.chatservice.domain.model.ContentType;

public record ChatMessageReqDto(
    ContentType contentType,
    String messageContent
) {

}
