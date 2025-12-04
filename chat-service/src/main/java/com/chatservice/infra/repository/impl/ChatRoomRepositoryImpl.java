package com.chatservice.infra.repository.impl;

import com.chatservice.domain.model.ChatRoom;
import com.chatservice.domain.repository.ChatRoomRepository;
import com.chatservice.infra.repository.jpa.ChatRoomJpaRepository;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class ChatRoomRepositoryImpl implements ChatRoomRepository {

  private final ChatRoomJpaRepository chatRoomJpaRepository;

  @Override
  public ChatRoom save(ChatRoom chatRoom) {
    return chatRoomJpaRepository.save(chatRoom);
  }

  @Override
  public Optional<ChatRoom> findDirectChatRoom(Long userId1, Long userId2) {
    return chatRoomJpaRepository.findDirectChatRoom(userId1, userId2);
  }
}
