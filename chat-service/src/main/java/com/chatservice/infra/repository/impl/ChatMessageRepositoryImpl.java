package com.chatservice.infra.repository.impl;

import com.chatservice.domain.model.ChatMessage;
import com.chatservice.domain.repository.ChatMessageRepository;
import com.chatservice.infra.repository.jpa.ChatMessageJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class ChatMessageRepositoryImpl implements ChatMessageRepository {

  private final ChatMessageJpaRepository chatMessageJpaRepository;

  @Override
  public ChatMessage save(ChatMessage chatMessage) {
    return chatMessageJpaRepository.save(chatMessage);
  }
}
