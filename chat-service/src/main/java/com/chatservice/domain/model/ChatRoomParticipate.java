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
public class ChatRoomParticipate {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(nullable = false)
  private Long chatRoomId;

  @Column(nullable = false)
  private Long userId;

  private boolean isBlocked;
  private LocalDateTime blockedAt;

  private boolean isLeft;
  private LocalDateTime leftAt;

  private LocalDateTime createdAt;
}

// TODO : ID 수정 필요