package com.chatservice.domain.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "p_chat_room_participates")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ChatParticipate {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(nullable = false)
  private Long chatRoomId;

  @Column(nullable = false)
  private Long userId;

//  @Column(nullable = false)
//  private boolean isBlocked;
//
//  private LocalDateTime blockedAt;

  @Column(nullable = false)
  private boolean isLeft;

  private LocalDateTime leftAt;

  @Column(nullable = false)
  private LocalDateTime createdAt;

  /**
   * 채팅 참여자 생성을 위한 정적 팩토리 메서드
   */
  public static ChatParticipate create(Long chatRoomId, Long userId) {
    ChatParticipate participate = new ChatParticipate();
    participate.chatRoomId = chatRoomId;
    participate.userId = userId;
    participate.createdAt = LocalDateTime.now();
    return participate;
  }
}

// TODO : ID 수정 필요