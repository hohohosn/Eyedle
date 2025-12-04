package com.chatservice.infra.repository.impl;

import static com.chatservice.domain.model.QChatParticipate.chatParticipate;
import static com.chatservice.domain.model.QChatRoom.chatRoom;

import com.chatservice.domain.model.ChatRoom;
import com.chatservice.domain.model.ChatRoomType;
import com.chatservice.domain.repository.ChatRoomRepository;
import com.chatservice.infra.repository.jpa.ChatRoomJpaRepository;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class ChatRoomRepositoryImpl implements ChatRoomRepository {

  private final ChatRoomJpaRepository chatRoomJpaRepository;
  private final JPAQueryFactory jpaQueryFactory;

  @Override
  public void save(ChatRoom chatRoom) {
    chatRoomJpaRepository.save(chatRoom);
  }

  @Override
  public Optional<ChatRoom> findById(Long id) {
    return chatRoomJpaRepository.findById(id);
  }

  @Override
  public Optional<ChatRoom> findDirectChatRoom(Long userId1, Long userId2) {
    ChatRoom room = jpaQueryFactory
        .select(chatRoom)
        .from(chatRoom)
        .join(chatParticipate)
        .on(chatParticipate.chatRoomId.eq(chatRoom.id).and(chatParticipate.userId.eq(userId1)))
        .join(chatParticipate, chatParticipate)
        .on(chatParticipate.chatRoomId.eq(chatRoom.id).and(chatParticipate.userId.eq(userId2)))
        .where(chatRoom.chatRoomType.eq(ChatRoomType.DIRECT))
        .fetchOne();
    return Optional.ofNullable(room);
  }

  @Override
  public List<ChatRoom> findChatRooms(Long userId, Long cursor, Pageable pageable) {
    return jpaQueryFactory
        .select(chatRoom)
        .from(chatRoom)
        .join(chatParticipate)
        .on(chatParticipate.chatRoomId.eq(chatRoom.id))
        .where(chatParticipate.userId.eq(userId).and(chatRoom.id.lt(cursor)))
        .orderBy(chatRoom.id.desc())
        .offset(pageable.getOffset())
        .limit(pageable.getPageSize())
        .fetch();
  }


}
