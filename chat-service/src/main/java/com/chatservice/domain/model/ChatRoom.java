package com.chatservice.domain.model;

import com.common.database.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "p_chat_rooms")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AttributeOverride(name = "updatedAt", column = @Column(name = "ignore_updated_at", insertable = false, updatable = false))
public class ChatRoom extends BaseTimeEntity {

  @Column(length = 20)
  private String chatRoomName;

  @Column(nullable = false)
  @Enumerated(EnumType.STRING)
  private ChatRoomType chatRoomType;

  @Column(nullable = false)
  @Enumerated(EnumType.STRING)
  private ChatRoomStatus chatRoomStatus;

  /**
   * 일대일 채팅방 생성을 위한 정적 팩토리 메서드
   */
  public static ChatRoom createOneToOne(ChatRoomStatus chatRoomStatus) {
    ChatRoom chatRoom = new ChatRoom();
    chatRoom.chatRoomType = ChatRoomType.DIRECT;
    chatRoom.chatRoomStatus = chatRoomStatus;
    return chatRoom;
  }

  public void changeRoomStatus(ChatRoomStatus chatRoomStatus) {
    this.chatRoomStatus = chatRoomStatus;
  }
}