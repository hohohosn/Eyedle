package com.chatservice.infra.repository.impl;

import com.chatservice.domain.model.ChatMessage;
import com.chatservice.domain.model.QChatMessage;
import com.chatservice.domain.repository.ChatMessageRepository;
import com.chatservice.infra.repository.jpa.ChatMessageJpaRepository;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class ChatMessageRepositoryImpl implements ChatMessageRepository {

  private final ChatMessageJpaRepository chatMessageJpaRepository;
  private final JPAQueryFactory jpaQueryFactory;

  @Override
  public void save(ChatMessage chatMessage) {
    chatMessageJpaRepository.save(chatMessage);
  }

  @Override
  public Optional<ChatMessage> findById(Long messageId) {
    return chatMessageJpaRepository.findById(messageId);
  }

  @Override
  public Map<Long, ChatMessage> findLastMessageByChatRoomIds(List<Long> chatRoomIds) {
    QChatMessage m1 = new QChatMessage("m1");
    QChatMessage m2 = new QChatMessage("m2");

    List<ChatMessage> lastMessages = jpaQueryFactory
        .select(m1)
        .from(m1)
        .where(m1.createdAt.eq(
                JPAExpressions
                    .select(m2.createdAt.max())
                    .from(m2)
                    .where(m2.chatRoomId.eq(m1.chatRoomId)))
            .and(m1.chatRoomId.in(chatRoomIds)))
        .orderBy(m1.chatRoomId.asc(), m1.id.desc())
        .fetch();

    return lastMessages.stream().collect(Collectors.toMap(ChatMessage::getChatRoomId, Function.identity()));
  }
}
