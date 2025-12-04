package com.chatservice.infra.repository.impl;

import com.chatservice.domain.model.ChatParticipate;
import com.chatservice.domain.repository.ChatParticipateRepository;
import com.chatservice.infra.repository.jpa.ChatParticipateJpaRepository;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import static com.chatservice.domain.model.QChatParticipate.chatParticipate;

@Repository
@RequiredArgsConstructor
public class ChatParticipateRepositoryImpl implements ChatParticipateRepository {

  private final ChatParticipateJpaRepository chatParticipateJpaRepository;
  private final JPAQueryFactory jpaQueryFactory;

  @Override
  public void save(ChatParticipate chatParticipate) {
    chatParticipateJpaRepository.save(chatParticipate);
  }

  @Override
  public boolean isLeft(Long chatRoomId, Long userId) {
    Integer result = jpaQueryFactory
        .selectOne()
        .from(chatParticipate)
        .where(chatParticipate.chatRoomId.eq(chatRoomId)
            .and(chatParticipate.userId.eq(userId))
            .and(chatParticipate.isLeft.isTrue()))
        .fetchFirst();
    return result != null;
  }

  @Override
  public Optional<ChatParticipate> findByChatRoomIdAndUserId(Long chatRoomId, Long userId) {
    return chatParticipateJpaRepository.findByChatRoomIdAndUserId(chatRoomId, userId);
  }

  @Override
  public boolean existsByChatRoomIdAndUserId(Long chatRoomId, Long userId) {
    return chatParticipateJpaRepository.existsByChatRoomIdAndUserId(chatRoomId, userId);
  }

  @Override
  public Long findOtherUserId(Long chatRoomId, Long currentUserId) {
    return jpaQueryFactory
        .select(chatParticipate.userId)
        .from(chatParticipate)
        .where(chatParticipate.chatRoomId.eq(chatRoomId)
            .and(chatParticipate.userId.ne(currentUserId))
            .and(chatParticipate.isLeft.eq(false)))
        .fetchOne();
  }
}
