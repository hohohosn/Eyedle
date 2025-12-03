package com.chatservice.domain.repository;

import com.chatservice.domain.model.ChatMessage;
import org.springframework.stereotype.Repository;

@Repository
public interface ChatMessageRepository {

  ChatMessage save(ChatMessage chatMessage);
}
