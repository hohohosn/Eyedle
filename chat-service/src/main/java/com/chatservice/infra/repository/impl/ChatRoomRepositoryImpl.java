package com.chatservice.infra.repository.impl;

import com.chatservice.domain.model.ChatRoom;
import com.chatservice.domain.repository.ChatRoomRepository;
import com.chatservice.infra.repository.jpa.ChatRoomJpaRepository;
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
}
