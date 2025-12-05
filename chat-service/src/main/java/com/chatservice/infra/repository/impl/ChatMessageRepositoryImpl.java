package com.chatservice.infra.repository.impl;

import com.chatservice.domain.model.ChatMessage;
import com.chatservice.domain.repository.ChatMessageRepository;
import com.chatservice.infra.repository.jpa.ChatMessageJpaRepository;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.List;
import java.util.Map;
import java.util.Optional;
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
  public void save(ChatMessage chatMessage) {
    chatMessageJpaRepository.save(chatMessage);
  }

  @Override
  public Optional<ChatMessage> findById(Long messageId) {
    return chatMessageJpaRepository.findById(messageId);
  }

  @Override
  public Map<Long, ChatMessage> findLastMessageByChatRoomIds(List<Long> chatRoomIds) {
    List<ChatMessage> messages = jpaQueryFactory
        .selectFrom(chatMessage)
        .where(chatMessage.chatRoomId.in(chatRoomIds))
        .orderBy(chatMessage.chatRoomId.asc(), chatMessage.createdAt.desc())
        .fetch();
    return messages.stream().collect(Collectors.toMap(ChatMessage::getChatRoomId, msg -> msg, (existing, replacement) -> existing));
  }
}
