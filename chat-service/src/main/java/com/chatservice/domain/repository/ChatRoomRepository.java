package com.chatservice.domain.repository;

import com.chatservice.domain.model.ChatRoom;
import java.util.Optional;
import org.springframework.stereotype.Repository;

@Repository
public interface ChatRoomRepository {

  ChatRoom save(ChatRoom chatRoom);

  Optional<ChatRoom> findDirectChatRoom(Long userId1, Long userId2);
}
