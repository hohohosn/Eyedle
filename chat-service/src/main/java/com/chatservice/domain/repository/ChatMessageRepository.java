package com.chatservice.domain.repository;

import com.chatservice.domain.model.ChatMessage;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.springframework.stereotype.Repository;

@Repository
public interface ChatMessageRepository {

  ChatMessage save(ChatMessage chatMessage);

  Optional<ChatMessage> findById(Long messageId);

  Map<Long, ChatMessage> findLastMessageByChatRoomIds(List<Long> chatRoomIds);

  List<ChatMessage> findChatMessagesBetween(Long chatRoomId, LocalDateTime from, LocalDateTime to, int pageSize);

  Long findNewMessageIdAfter(Long chatRoomId, Long lastReadId);

  long countMessagesAfter(Long chatRoomId, Long lastReadId);
}
