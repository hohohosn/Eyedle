package com.chatservice.application.service;

import static com.chatservice.common.ChatErrorCode.CHAT_PARTICIPATE_NOT_FOUND;
import static com.chatservice.common.ChatErrorCode.CHAT_ROOM_NOT_FOUND;
import static com.chatservice.domain.model.ChatRoomStatus.OPEN;
import static com.chatservice.domain.model.ChatRoomStatus.REQUESTED;

import com.chatservice.common.ChatErrorCode;
import com.chatservice.domain.model.ChatParticipate;
import com.chatservice.domain.model.ChatRoom;
import com.chatservice.domain.repository.ChatParticipateRepository;
import com.chatservice.domain.repository.ChatRoomRepository;
import com.chatservice.presentation.request.CreateChatRoomReqDto;
import com.chatservice.presentation.response.CreateChatRoomResDto;
import com.common.exception.CustomException;
import java.util.stream.Stream;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ChatService {

  private final ChatRoomRepository chatRoomRepository;
  private final ChatParticipateRepository chatParticipateRepository;

  /**
   * 새 채팅 생성
   */
  @Transactional
  public CreateChatRoomResDto createDirectChatRoom(Long userId, CreateChatRoomReqDto reqDto) {

    // 두 유저가 같은 사람인지 확인
    if (reqDto.receiverId().equals(userId)) {
      throw new CustomException(ChatErrorCode.SELF_CHAT_NOT_ALLOWED);
    }

    // TODO: 존재하는 유저인지 확인

    // TODO: 차단 관계의 유저인지 확인

    return chatRoomRepository.findDirectChatRoom(userId, reqDto.receiverId())
        .map(existingChatRoom -> {
          boolean meLeft = chatParticipateRepository.isLeft(existingChatRoom.getId(), userId);
          boolean receiverLeft = chatParticipateRepository.isLeft(existingChatRoom.getId(), reqDto.receiverId());

          // 둘 다 방을 떠났으면 새 채팅방 생성
          return (meLeft && receiverLeft) ? createNewChatRoom(userId, reqDto.receiverId())
              : CreateChatRoomResDto.from(existingChatRoom);
        })
        // 기존 방이 없으면 새 채팅방 생성
        .orElseGet(() -> createNewChatRoom(userId, reqDto.receiverId()));
  }

  /**
   * 채팅 수락
   */
  @Transactional
  public void acceptChatRoom(Long chatRoomId, Long userId) {
    ChatRoom chatRoom = chatRoomRepository.findById(chatRoomId)
        .orElseThrow(() -> new CustomException(CHAT_ROOM_NOT_FOUND));

    // REQUESTED 상태를 OPEN으로 전환
    if (chatRoom.getChatRoomStatus() == REQUESTED) {
      chatRoom.changeRoomStatus(OPEN);
    }

    ChatParticipate chatParticipate = getChatParticipate(chatRoomId, userId);

    // 채팅 참여
    chatParticipate.join();
  }

  /**
   * 채팅 거절
   */
  @Transactional
  public void rejectChatRoom(Long chatRoomId, Long userId) {
    ChatParticipate chatParticipate = getChatParticipate(chatRoomId, userId);

    // 채팅 거절
    chatParticipate.leave();
  }

  private ChatParticipate getChatParticipate(Long chatRoomId, Long userId) {
    return chatParticipateRepository.findByChatRoomIdAndUserId(chatRoomId, userId)
        .orElseThrow(() -> new CustomException(CHAT_PARTICIPATE_NOT_FOUND));
  }

  private CreateChatRoomResDto createNewChatRoom(Long userId, Long receiverId) {

    // TODO: 팔로우 관계 확인

    // 채팅방 생성 -> 팔로우 관계에 따라 생성되는 방 상태 다름
    ChatRoom newChatRoom = ChatRoom.createOneToOne(OPEN);
    chatRoomRepository.save(newChatRoom);

    // 채팅 참여자 생성
    Stream.of(userId, receiverId)
        .map(id -> ChatParticipate.create(newChatRoom.getId(), id))
        .forEach(chatParticipateRepository::save);

    return CreateChatRoomResDto.from(newChatRoom);
  }
}