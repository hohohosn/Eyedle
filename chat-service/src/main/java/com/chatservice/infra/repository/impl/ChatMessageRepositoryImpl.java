package com.chatservice.infra.repository.impl;

import com.chatservice.domain.model.ChatMessage;
import com.chatservice.domain.model.QChatMessage;
import com.chatservice.domain.repository.ChatMessageRepository;
import com.chatservice.infra.repository.jpa.ChatMessageJpaRepository;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import static com.chatservice.domain.model.QChatMessage.chatMessage;

@Repository
@RequiredArgsConstructor
public class ChatMessageRepositoryImpl implements ChatMessageRepository {

  private final ChatMessageJpaRepository chatMessageJpaRepository;
  private final JPAQueryFactory jpaQueryFactory;

  @Override
  public ChatMessage save(ChatMessage chatMessage) {

    return chatMessageJpaRepository.save(chatMessage);
  }

  @Override
  public Optional<ChatMessage> findById(Long messageId) {

    return chatMessageJpaRepository.findById(messageId);
  }

  @Override
  public Map<Long, ChatMessage> findLastMessageByChatRoomIds(List<Long> chatRoomIds) {

    QChatMessage m1 = new QChatMessage("m1");    // 가져올 메시지
    QChatMessage m2 = new QChatMessage("m2");    // 서브 쿼리용 메시지 -> 각 채팅방의 최신 메시지 시각 계산

    List<ChatMessage> lastMessages = jpaQueryFactory
        .select(m1)
        .from(m1)
        // 각 채팅방에서 가장 최신 메시지인지 확인
        .where(m1.createdAt.eq(                   // m1의 메시지 중에서 서브쿼리에서 나온 최대 시간과 같은 메시지 선택
                JPAExpressions
                    .select(m2.createdAt.max())   // m1과 같은 채팅방 안에서 나온 가장 최신 시간
                    .from(m2)
                    .where(m2.chatRoomId.eq(m1.chatRoomId)))
            .and(m1.chatRoomId.in(chatRoomIds)))
        .orderBy(m1.chatRoomId.asc(), m1.id.desc())
        .fetch();

    return lastMessages.stream().collect(Collectors.toMap(ChatMessage::getChatRoomId, Function.identity()));
  }

  @Override
  public List<ChatMessage> findChatMessagesBetween(Long chatRoomId, LocalDateTime from, LocalDateTime to, int pageSize) {

    return jpaQueryFactory
        .selectFrom(chatMessage)
        .where(chatMessage.chatRoomId.eq(chatRoomId).and(chatMessage.createdAt.between(from, to)))
        .orderBy(chatMessage.createdAt.desc())
        .limit(pageSize)
        .fetch();
  }
}
