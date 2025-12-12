package com.chatservice.domain.repository;

import com.chatservice.domain.model.ChatRoom;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

@Repository
public interface ChatRoomRepository {

  void save(ChatRoom chatRoom);

  Optional<ChatRoom> findById(Long id);

  Optional<ChatRoom> findDirectChatRoom(Long userId1, Long userId2);

  List<ChatRoom> findChatRooms(Long userId, Long cursor, Pageable pageable);
}
