package com.chatservice.infra.repository.jpa;

import com.chatservice.domain.model.ChatRoom;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ChatRoomJpaRepository extends JpaRepository<ChatRoom, Long> {

}
