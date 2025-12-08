package com.chatservice.domain.model;

import com.common.database.BaseTimeEntity;
import jakarta.persistence.*;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(
    name = "p_chat_messages",
    indexes = {
        @Index(name = "idx_chat_room_and_created_at", columnList = "chat_room_id, created_at DESC")
    }
)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AttributeOverride(name = "updatedAt", column = @Column(name = "ignore_updated_at", insertable = false, updatable = false))
public class ChatMessage extends BaseTimeEntity {

  @Column(nullable = false)
  private Long chatRoomId;

  @Column(nullable = false)
  private Long userId;

  @Enumerated(EnumType.STRING)
  private ContentType contentType;

  @Column(nullable = false, length = 500)
  private String messageContent;

  private boolean isReported;

  private LocalDateTime deletedAt;

  /**
   * 채팅 메세지 생성을 위한 정적 팩토리 메서드
   */
  public static ChatMessage create(Long chatRoomId, Long userId, ContentType contentType, String messageContent) {
    ChatMessage chatMessage = new ChatMessage();
    chatMessage.chatRoomId = chatRoomId;
    chatMessage.userId = userId;
    chatMessage.contentType = contentType;
    chatMessage.messageContent = messageContent;
    chatMessage.isReported = false;
    return chatMessage;
  }

  public void delete(LocalDateTime deletedAt) {
    this.deletedAt = deletedAt;
  }
}