package com.chatservice.domain.repository;

import com.chatservice.domain.model.ChatParticipate;
import org.springframework.stereotype.Repository;

@Repository
public interface ChatParticipateRepository {

  ChatParticipate save(ChatParticipate chatParticipate);
}
