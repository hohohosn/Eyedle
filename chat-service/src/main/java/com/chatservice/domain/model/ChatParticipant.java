package com.chatservice.domain.model;

import com.common.database.BaseTimeEntity;
import jakarta.persistence.*;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(
    name = "p_chat_participants",
    uniqueConstraints = {
        @UniqueConstraint(columnNames = {"chat_room_id", "user_id"})
    }
)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AttributeOverride(name = "updatedAt", column = @Column(name = "ignore_updated_at", insertable = false, updatable = false))
public class ChatParticipant extends BaseTimeEntity {

  @Column(nullable = false)
  private Long chatRoomId;

  @Column(nullable = false)
  private Long userId;

  private boolean isLeft;

  private LocalDateTime leftAt;

  /**
   * 채팅 참여자 생성을 위한 정적 팩토리 메서드
   */
  public static ChatParticipant create(Long chatRoomId, Long userId, boolean isLeft) {
    ChatParticipant participant = new ChatParticipant();
    participant.chatRoomId = chatRoomId;
    participant.userId = userId;
    participant.isLeft = isLeft;
    return participant;
  }

  /**
   * 채팅 참여자의 채팅 참여 여부
   */
  public void leave() {
    this.isLeft = true;
    this.leftAt = LocalDateTime.now();
  }

  public void join() {
    this.isLeft = false;
  }
}