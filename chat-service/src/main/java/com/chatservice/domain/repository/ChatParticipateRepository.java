package com.chatservice.domain.repository;

import com.chatservice.domain.model.ChatParticipate;
import java.util.Optional;
import org.springframework.stereotype.Repository;

@Repository
public interface ChatParticipateRepository {

  ChatParticipate save(ChatParticipate chatParticipate);

  boolean isLeft(Long chatRoomId, Long userId);

  Optional<ChatParticipate> findByChatRoomIdAndUserId(Long chatRoomId, Long userId);
}
