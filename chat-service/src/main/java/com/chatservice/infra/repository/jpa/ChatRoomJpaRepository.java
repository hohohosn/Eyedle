package com.chatservice.infra.repository.jpa;

import com.chatservice.domain.model.ChatRoom;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ChatRoomJpaRepository extends JpaRepository<ChatRoom, Long> {

  @Query("""
      SELECT r FROM ChatRoom r
      JOIN ChatParticipate p1 ON p1.chatRoomId = r.id AND p1.userId = :userId1
      JOIN ChatParticipate p2 ON p2.chatRoomId = r.id AND p2.userId = :userId2
      WHERE r.chatRoomType = 'DIRECT'
      """)
  Optional<ChatRoom> findDirectChatRoom(
      @Param("userId1") Long userId1, @Param("userId2") Long userId2
  );
}
