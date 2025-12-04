package com.chatservice.infra.repository.jpa;

import com.chatservice.domain.model.ChatMessage;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ChatMessageJpaRepository extends JpaRepository<ChatMessage, Long> {

  Optional<ChatMessage> findTopByChatRoomIdOrderByIdDesc(Long chatRoomId);
}
