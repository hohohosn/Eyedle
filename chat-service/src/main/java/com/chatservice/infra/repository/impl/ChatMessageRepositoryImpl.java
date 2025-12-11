package com.chatservice.infra.repository.impl;

import com.chatservice.domain.model.ChatMessage;
import com.chatservice.domain.repository.ChatMessageRepository;
import com.chatservice.infra.repository.jpa.ChatMessageJpaRepository;
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

    List<ChatMessage> lastMessages = jpaQueryFactory
        .select(chatMessage)
        .from(chatMessage)
        .where(chatMessage.chatRoomId.in(chatRoomIds))
        .orderBy(
            chatMessage.chatRoomId.asc(),     // DISTINCT ON 기준
            chatMessage.createdAt.desc()      // 최신 메세지 정렬
        )
        .distinct()
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
