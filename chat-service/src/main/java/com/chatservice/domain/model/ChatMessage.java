package com.chatservice.domain.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "p_chat_messages")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ChatMessage {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(nullable = false)
  private Long chatRoomId;

  @Column(nullable = false)
  private Long userId;

  @Enumerated(EnumType.STRING)
  private ContentType contentType;

  @Column(nullable = false)
  private String messageContent;

  private boolean isReported = false;

  private LocalDateTime createdAt;
  private LocalDateTime deletedAt;
}

// TODO : ID 수정 필요
