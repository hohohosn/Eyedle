package com.chatservice.domain.repository;

import com.chatservice.domain.model.ChatParticipant;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.springframework.stereotype.Repository;

@Repository
public interface ChatParticipantRepository {

  void save(ChatParticipant chatParticipant);

  boolean isLeft(Long chatRoomId, Long userId);

  Optional<ChatParticipant> findByChatRoomIdAndUserId(Long chatRoomId, Long userId);

  Map<Long, Long> findOtherUserIds(List<Long> chatRoomIds, Long currentUserId);
}
