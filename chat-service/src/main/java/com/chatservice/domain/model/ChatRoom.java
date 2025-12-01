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
@Table(name = "p_chat_rooms")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ChatRoom {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(length = 20)
  private String chatRoomName;

  @Column(nullable = false)
  @Enumerated(EnumType.STRING)
  private ChatRoomType chatRoomType;

  @Column(nullable = false)
  @Enumerated(EnumType.STRING)
  private ChatRoomStatus chatRoomStatus;

//  private LocalDateTime lastMessageAt;

  @Column(nullable = false)
  private LocalDateTime createdAt;

  private LocalDateTime updatedAt;
}

// TODO : ID 수정 필요