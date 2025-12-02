package com.chatservice.infra.repository.impl;

import com.chatservice.domain.model.ChatParticipate;
import com.chatservice.domain.repository.ChatParticipateRepository;
import com.chatservice.infra.repository.jpa.ChatParticipateJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class ChatParticipateRepositoryImpl implements ChatParticipateRepository {

  private final ChatParticipateJpaRepository chatParticipateJpaRepository;

  @Override
  public ChatParticipate save(ChatParticipate chatParticipate) {
    return chatParticipateJpaRepository.save(chatParticipate);
  }
}
