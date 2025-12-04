package com.chatservice.infra.repository.impl;

import com.chatservice.domain.model.ChatParticipant;
import com.chatservice.domain.repository.ChatParticipantRepository;
import com.chatservice.infra.repository.jpa.ChatParticipantJpaRepository;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import static com.chatservice.domain.model.QChatParticipant.chatParticipant;

@Repository
@RequiredArgsConstructor
public class ChatParticipantRepositoryImpl implements ChatParticipantRepository {

  private final ChatParticipantJpaRepository chatParticipantJpaRepository;
  private final JPAQueryFactory jpaQueryFactory;

  @Override
  public void save(ChatParticipant chatParticipant) {
    chatParticipantJpaRepository.save(chatParticipant);
  }

  @Override
  public boolean isLeft(Long chatRoomId, Long userId) {
    Integer result = jpaQueryFactory
        .selectOne()
        .from(chatParticipant)
        .where(chatParticipant.chatRoomId.eq(chatRoomId)
            .and(chatParticipant.userId.eq(userId))
            .and(chatParticipant.isLeft.isTrue()))
        .fetchFirst();
    return result != null;
  }

  @Override
  public Optional<ChatParticipant> findByChatRoomIdAndUserId(Long chatRoomId, Long userId) {
    return chatParticipantJpaRepository.findByChatRoomIdAndUserId(chatRoomId, userId);
  }

  @Override
  public boolean existsByChatRoomIdAndUserId(Long chatRoomId, Long userId) {
    return chatParticipantJpaRepository.existsByChatRoomIdAndUserId(chatRoomId, userId);
  }

  @Override
  public Long findOtherUserId(Long chatRoomId, Long currentUserId) {
    return jpaQueryFactory
        .select(chatParticipant.userId)
        .from(chatParticipant)
        .where(chatParticipant.chatRoomId.eq(chatRoomId)
            .and(chatParticipant.userId.ne(currentUserId))
            .and(chatParticipant.isLeft.eq(false)))
        .fetchOne();
  }
}
