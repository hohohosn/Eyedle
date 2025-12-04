package com.chatservice.application.service;

import com.chatservice.application.dto.ChatRoomInfo;
import com.chatservice.application.dto.UserInfo;
import com.chatservice.domain.model.ChatMessage;
import com.chatservice.domain.model.ChatParticipate;
import com.chatservice.domain.model.ChatRoom;
import com.chatservice.domain.model.ChatRoomStatus;
import com.chatservice.domain.repository.ChatMessageRepository;
import com.chatservice.domain.repository.ChatParticipateRepository;
import com.chatservice.domain.repository.ChatRoomRepository;
import com.chatservice.infra.client.BlockClient;
import com.chatservice.infra.client.FollowClient;
import com.chatservice.infra.client.UserClient;
import com.chatservice.presentation.request.CreateChatRoomReqDto;
import com.chatservice.presentation.response.ChatRoomCursorResDto;
import com.chatservice.presentation.response.CreateChatRoomResDto;
import com.common.exception.CustomException;
import java.util.List;
import java.util.stream.Stream;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.chatservice.common.ChatErrorCode.*;
import static com.chatservice.domain.model.ChatRoomStatus.OPEN;
import static com.chatservice.domain.model.ChatRoomStatus.REQUESTED;

@Service
@RequiredArgsConstructor
public class ChatService {

  private final static int PAGE_SIZE = 20;
  private final ChatRoomRepository chatRoomRepository;
  private final ChatParticipateRepository chatParticipateRepository;
  private final ChatMessageRepository chatMessageRepository;
  private final UserClient userClient;
  private final BlockClient blockClient;
  private final FollowClient followClient;

  /**
   * 새 채팅 생성
   */
  @Transactional
  public CreateChatRoomResDto createDirectChatRoom(Long userId, CreateChatRoomReqDto reqDto) {

    // 두 유저가 같은 사람인지 확인
    if (reqDto.receiverId().equals(userId)) {
      throw new CustomException(SELF_CHAT_NOT_ALLOWED);
    }

    // 상대가 존재하는 유저인지 확인
    UserInfo receiverInfo = userClient.getUserInfo(reqDto.receiverId());
    if (receiverInfo == null) {
      throw new CustomException(USER_NOT_FOUND);
    }

    // 내가 상대를 차단했는지
    boolean blockedByMe = blockClient.isBlocked(userId, reqDto.receiverId());
    //상대가 나를 차단했는지
    boolean blockedMe = blockClient.isBlocked(reqDto.receiverId(), userId);

    if (blockedByMe || blockedMe) {
      throw new CustomException(BLOCKED_USER);
    }

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

  /**
   * 채팅방 목록 조회
   */
  @Transactional(readOnly = true)
  public ChatRoomCursorResDto getChatRoomList(Long userId, Long cursor) {
    // 커서 값 없으면 가장 큰 값으로 초기화(최신 방부터 조회)
    Long effectiveCursor = (cursor == null) ? Long.MAX_VALUE : cursor;

    Pageable pageable = PageRequest.of(0, PAGE_SIZE);

    // 커서 기준 채팅방 조회
    List<ChatRoom> chatRooms = chatRoomRepository.findChatRooms(userId, effectiveCursor, pageable);

    List<ChatRoomInfo> chatRoomInfos = chatRooms.stream().map(r -> createChatRoomInfo(r, userId)).toList();

    // 다음 페이지 조회용 커서
    Long nextCursor = (chatRooms.size() < PAGE_SIZE) ? null : chatRooms.get(chatRooms.size() - 1).getId();

    return ChatRoomCursorResDto.of(chatRoomInfos, nextCursor);
  }

  /**
   * 채팅방 나가기
   */
  @Transactional
  public void leaveChatRoom(Long chatRoomId, Long userId) {
    ChatParticipate chatParticipate = getChatParticipate(chatRoomId, userId);

    if (chatParticipate.isLeft()) {
      throw new CustomException(ALREADY_LEFT_CHAT_ROOM);
    }

    chatParticipate.leave();
  }

  private ChatRoomInfo createChatRoomInfo(ChatRoom chatRoom, Long userId) {
    Long chatRoomId = chatRoom.getId();

    Long receiverId = chatParticipateRepository.findOtherUserId(chatRoomId, userId);

    UserInfo receiverInfo = userClient.getUserInfo(receiverId);

    ChatMessage lastMessage = chatMessageRepository.findLastMessage(chatRoomId).orElse(null);

    return ChatRoomInfo.of(chatRoomId, receiverInfo, lastMessage);
  }

  private ChatParticipate getChatParticipate(Long chatRoomId, Long userId) {
    return chatParticipateRepository.findByChatRoomIdAndUserId(chatRoomId, userId)
        .orElseThrow(() -> new CustomException(CHAT_PARTICIPATE_NOT_FOUND));
  }

  private CreateChatRoomResDto createNewChatRoom(Long userId, Long receiverId) {

    // 팔로우 관계 확인
    boolean iFollowReceiver = followClient.isFollowing(userId, receiverId);
    boolean receiverFollowMe = followClient.isFollowing(receiverId, userId);

    // 한쪽이라도 팔로우 중이면 바로 OPEN
    ChatRoomStatus chatRoomStatus = (iFollowReceiver || receiverFollowMe) ? OPEN : REQUESTED;

    // 채팅방 생성
    ChatRoom newChatRoom = ChatRoom.createOneToOne(chatRoomStatus);
    chatRoomRepository.save(newChatRoom);

    // 채팅 참여자 생성
    Stream.of(userId, receiverId)
        .forEach(id -> {
          // 중복 참여 확인
          if (chatParticipateRepository.existsByChatRoomIdAndUserId(newChatRoom.getId(), id)) {
            throw new CustomException(DUPLICATE_CHAT_PARTICIPATE);
          }

          chatParticipateRepository.save(ChatParticipate.create(newChatRoom.getId(), id));
        });

    return CreateChatRoomResDto.from(newChatRoom);
  }
}