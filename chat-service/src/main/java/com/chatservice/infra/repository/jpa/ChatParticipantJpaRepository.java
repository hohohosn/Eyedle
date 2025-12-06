package com.chatservice.infra.repository.jpa;

import com.chatservice.domain.model.ChatParticipant;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ChatParticipantJpaRepository extends JpaRepository<ChatParticipant, Long> {

  Optional<ChatParticipant> findByChatRoomIdAndUserId(Long chatRoomId, Long userId);

  boolean existsByChatRoomIdAndUserId(Long chatRoomId, Long userId);
}
