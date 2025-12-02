package com.chatservice.domain.repository;

import com.chatservice.domain.model.ChatRoom;
import org.springframework.stereotype.Repository;

@Repository
public interface ChatRoomRepository {

  ChatRoom save(ChatRoom chatRoom);
}
