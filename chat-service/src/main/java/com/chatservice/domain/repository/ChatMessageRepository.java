package com.chatservice.domain.repository;

import com.chatservice.domain.model.ChatMessage;
import java.util.Optional;
import org.springframework.stereotype.Repository;

@Repository
public interface ChatMessageRepository {

  void save(ChatMessage chatMessage);

  Optional<ChatMessage> findLastMessage(Long chatRoomId);
}
