package com.chatservice.domain.model;

import com.common.database.BaseTimeEntity;
import jakarta.persistence.AttributeOverride;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "p_chat_participates")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AttributeOverride(name = "updatedAt", column = @Column(name = "ignore_updated_at", insertable = false, updatable = false))
public class ChatParticipate extends BaseTimeEntity {

  @Column(nullable = false)
  private Long chatRoomId;

  @Column(nullable = false)
  private Long userId;

  @Column(nullable = false)
  private boolean isLeft;

  private LocalDateTime leftAt;

  /**
   * 채팅 참여자 생성을 위한 정적 팩토리 메서드
   */
  public static ChatParticipate create(Long chatRoomId, Long userId) {
    ChatParticipate participate = new ChatParticipate();
    participate.chatRoomId = chatRoomId;
    participate.userId = userId;
    return participate;
  }

  /**
   * 채팅 참여자의 채팅 참여 여부
   */
  public void leave() {
    this.isLeft = true;
  }

  public void join() {
    this.isLeft = false;
  }
}