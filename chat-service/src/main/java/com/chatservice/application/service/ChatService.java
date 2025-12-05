package com.chatservice.application.service;

import com.chatservice.application.dto.ChatRoomInfo;
import com.chatservice.application.dto.UserInfo;
import com.chatservice.domain.model.ChatMessage;
import com.chatservice.domain.model.ChatParticipant;
import com.chatservice.domain.model.ChatRoom;
import com.chatservice.domain.model.ChatRoomStatus;
import com.chatservice.domain.repository.ChatMessageRepository;
import com.chatservice.domain.repository.ChatParticipantRepository;
import com.chatservice.domain.repository.ChatRoomRepository;
import com.chatservice.infra.client.BlockClient;
import com.chatservice.infra.client.FollowClient;
import com.chatservice.infra.client.UserClient;
import com.chatservice.presentation.request.CreateChatRoomReqDto;
import com.chatservice.presentation.response.ChatRoomCursorResDto;
import com.chatservice.presentation.response.CreateChatRoomResDto;
import com.common.exception.CustomException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
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
  private final ChatParticipantRepository chatParticipantRepository;
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
          boolean meLeft = chatParticipantRepository.isLeft(existingChatRoom.getId(), userId);
          boolean receiverLeft = chatParticipantRepository.isLeft(existingChatRoom.getId(), reqDto.receiverId());

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

    ChatParticipant chatParticipant = getChatParticipate(chatRoomId, userId);

    // 채팅 참여
    chatParticipant.join();
  }

  /**
   * 채팅 거절
   */
  @Transactional
  public void rejectChatRoom(Long chatRoomId, Long userId) {
    ChatParticipant chatParticipant = getChatParticipate(chatRoomId, userId);

    // 채팅 거절
    chatParticipant.leave();
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
    if (chatRooms.isEmpty()) {
      return ChatRoomCursorResDto.of(List.of(), null);
    }

    // chat room id 목록
    List<Long> chatRoomIds = chatRooms.stream().map(ChatRoom::getId).toList();

    // 채팅 상대의 Id 한번에 조회
    Map<Long, Long> chatRoomToOtherUserId = chatParticipantRepository.findOtherUserIds(chatRoomIds, userId);

    // 채팅 상대 정보 조회
    List<Long> otherUserIds = new ArrayList<>(chatRoomToOtherUserId.values());
    Map<Long, UserInfo> userInfoMap = userClient.getUserInfos(otherUserIds);

    // 마지막 메세지 조회
    Map<Long, ChatMessage> lastMessageMap = chatMessageRepository.findLastMessageByChatRoomIds(chatRoomIds);

    // ChatRoomInfo 매핑
    List<ChatRoomInfo> chatRoomInfos = chatRooms.stream()
        .map(r -> {
          Long receiverId = chatRoomToOtherUserId.get(r.getId());
          UserInfo receiverInfo = userInfoMap.get(receiverId);
          ChatMessage lastMessage = lastMessageMap.get(r.getId());
          return ChatRoomInfo.of(r.getId(), receiverInfo, lastMessage);
        }).toList();

    // 다음 커서
    Long nextCursor = (chatRooms.size() < PAGE_SIZE) ? null : chatRooms.get(chatRooms.size() - 1).getId();

    return ChatRoomCursorResDto.of(chatRoomInfos, nextCursor);
  }

  /**
   * 채팅방 나가기
   */
  @Transactional
  public void leaveChatRoom(Long chatRoomId, Long userId) {
    ChatParticipant chatParticipant = getChatParticipate(chatRoomId, userId);

    if (chatParticipant.isLeft()) {
      throw new CustomException(ALREADY_LEFT_CHAT_ROOM);
    }

    chatParticipant.leave();
  }

  @Transactional
  public void deleteMessage(Long chatRoomId, Long messageId, Long userId) {
    ChatMessage chatMessage = chatMessageRepository.findById(messageId).orElseThrow(() -> new CustomException(CHAT_MESSAGE_NOT_FOUND));

    // 다른 채팅방 메세지 삭제 방지
    if (!chatMessage.getChatRoomId().equals(chatRoomId)) {
      throw new CustomException(MESSAGE_NOT_IN_CHATROOM);
    }

    // 본인이 보낸 메세지인지 확인
    if (!chatMessage.getUserId().equals(userId)) {
      throw new CustomException(NOT_MESSAGE_OWNER);
    }

    // 이미 삭제된 메세지인지 확인
    if (chatMessage.getDeletedAt() != null) {
      throw new CustomException(MESSAGE_ALREADY_DELETED);
    }

    chatMessage.delete();
  }

  private ChatParticipant getChatParticipate(Long chatRoomId, Long userId) {
    return chatParticipantRepository.findByChatRoomIdAndUserId(chatRoomId, userId)
        .orElseThrow(() -> new CustomException(CHAT_PARTICIPANT_NOT_FOUND));
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
    ChatParticipant sender = ChatParticipant.create(newChatRoom.getId(), userId, false);
    ChatParticipant receiver = ChatParticipant.create(newChatRoom.getId(), receiverId, chatRoomStatus == REQUESTED);   // 수신자는 아직 참여하지 않음
    chatParticipantRepository.save(sender);
    chatParticipantRepository.save(receiver);

    return CreateChatRoomResDto.from(newChatRoom);
  }
}