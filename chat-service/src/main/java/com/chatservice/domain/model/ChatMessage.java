package com.chatservice.domain.model;

import com.common.database.BaseTimeEntity;
import jakarta.persistence.AttributeOverride;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
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
}