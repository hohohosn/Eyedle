package com.chatservice.infra.repository.jpa;

import com.chatservice.domain.model.ChatParticipate;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ChatParticipateJpaRepository extends JpaRepository<ChatParticipate, Long> {

  Optional<ChatParticipate> findByChatRoomIdAndUserId(Long chatRoomId, Long userId);

  boolean existsByChatRoomIdAndUserId(Long chatRoomId, Long userId);
}
