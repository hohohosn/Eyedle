package com.chatservice.application.service;

import com.chatservice.application.dto.ChatRoomInfo;
import com.chatservice.application.dto.UserInfo;
import com.chatservice.domain.model.ChatMessage;
import com.chatservice.domain.model.ChatParticipant;
import com.chatservice.domain.model.ChatRoom;
import com.chatservice.domain.model.ChatRoomStatus;
import com.chatservice.domain.repository.ChatMessageReadRepository;
import com.chatservice.domain.repository.ChatMessageRepository;
import com.chatservice.domain.repository.ChatParticipantRepository;
import com.chatservice.domain.repository.ChatRoomRepository;
import com.chatservice.infra.client.BlockClient;
import com.chatservice.infra.client.FollowClient;
import com.chatservice.infra.client.UserClient;
import com.chatservice.infra.repository.redis.RedisChatMessageRepository;
import com.chatservice.presentation.request.ChatMessageReqDto;
import com.chatservice.presentation.request.CreateChatRoomReqDto;
import com.chatservice.presentation.response.ChatMessageResDto;
import com.chatservice.presentation.response.ChatRoomCursorResDto;
import com.chatservice.presentation.response.CreateChatRoomResDto;
import com.common.exception.CustomException;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.chatservice.common.ChatErrorCode.*;
import static com.chatservice.domain.model.ChatRoomStatus.OPEN;
import static com.chatservice.domain.model.ChatRoomStatus.REQUESTED;
import static java.lang.Long.MAX_VALUE;
import static java.time.Duration.ofDays;

@Slf4j
@Service
@RequiredArgsConstructor
public class ChatService {

  private final ChatRoomRepository chatRoomRepository;
  private final ChatParticipantRepository chatParticipantRepository;
  private final ChatMessageRepository chatMessageRepository;
  private final ChatMessageReadRepository chatMessageReadRepository;
  private final RedisChatMessageRepository redisChatMessageRepository;
  private final UserClient userClient;
  private final BlockClient blockClient;
  private final FollowClient followClient;

  /**
   * 새 채팅 생성
   */
  @Transactional
  public CreateChatRoomResDto createDirectChatRoom(Long userId, CreateChatRoomReqDto reqDto) {

    // 유효성 검증
    validateDirectChatRequest(userId, reqDto);

    return chatRoomRepository.findDirectChatRoom(userId, reqDto.receiverId())
        .map(existingChatRoom -> handleExistingDirectChatRoom(existingChatRoom, userId, reqDto.receiverId()))
        // 기존 방이 없으면 새 채팅방 생성
        .orElseGet(() -> createNewChatRoom(userId, reqDto.receiverId()));
  }

  /**
   * 채팅 수락
   */
  @Transactional
  public void acceptChatRoom(Long chatRoomId, Long userId) {

    ChatRoom chatRoom = getChatRoom(chatRoomId);
    ChatParticipant chatParticipant = getChatParticipant(chatRoomId, userId);

    if (chatRoom.getChatRoomStatus() == OPEN) {
      throw new CustomException(ALREADY_OPEN_CHAT_ROOM_STATUS);
    }

    // REQUESTED 상태를 OPEN으로 전환
    if (chatRoom.getChatRoomStatus() != REQUESTED) {
      throw new CustomException(CANNOT_OPEN_CHAT_ROOM_STATUS);
    }

    // 채팅방 상태 변경
    chatRoom.changeRoomStatus(OPEN);

    // 채팅 참여
    chatParticipant.join();
  }

  /**
   * 채팅 거절
   */
  @Transactional
  public void rejectChatRoom(Long chatRoomId, Long userId) {

    ChatRoom chatRoom = getChatRoom(chatRoomId);
    ChatParticipant chatParticipant = getChatParticipant(chatRoomId, userId);

    // 채팅방 상태가 요청 상태일 때만 거절 가능
    if (chatRoom.getChatRoomStatus() != REQUESTED) {
      throw new CustomException(CANNOT_REJECT_CHAT_ROOM);
    }

    // 이미 나간 채팅방은 거절 불가
    if (chatParticipant.isLeft()) {
      throw new CustomException(ALREADY_LEFT_CHAT_ROOM);
    }

    // 채팅 거절
    chatParticipant.leave();
  }

  /**
   * 채팅방 목록 조회
   */
  @Transactional(readOnly = true)
  public ChatRoomCursorResDto getChatRoomList(Long userId, Long cursor, int pageSize) {
    // 커서 값 없으면 가장 큰 값으로 초기화(최신 방부터 조회)
    Long effectiveCursor = (cursor == null) ? MAX_VALUE : cursor;
    Pageable pageable = PageRequest.of(0, pageSize);

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

    // 마지막 메시지 조회
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
    Long nextCursor = (chatRooms.size() < pageSize) ? null : chatRooms.get(chatRooms.size() - 1).getId();

    return ChatRoomCursorResDto.of(chatRoomInfos, nextCursor);
  }

  /**
   * 채팅방 나가기
   */
  @Transactional
  public void leaveChatRoom(Long chatRoomId, Long userId) {

    ChatParticipant chatParticipant = getChatParticipant(chatRoomId, userId);

    // 이미 나간 채팅방인지 확인
    if (chatParticipant.isLeft()) {
      throw new CustomException(ALREADY_LEFT_CHAT_ROOM);
    }

    chatParticipant.leave();
  }

  /**
   * 채팅 메시지 삭제
   */
  @Transactional
  public void deleteMessage(Long chatRoomId, Long messageId, Long userId) {

    ChatMessage chatMessage = chatMessageRepository.findById(messageId).orElseThrow(() -> new CustomException(CHAT_MESSAGE_NOT_FOUND));

    // 다른 채팅방 메시지 삭제 방지
    if (!chatMessage.getChatRoomId().equals(chatRoomId)) {
      throw new CustomException(MESSAGE_NOT_IN_CHATROOM);
    }

    // 본인이 보낸 메시지인지 확인
    if (!chatMessage.getUserId().equals(userId)) {
      throw new CustomException(NOT_MESSAGE_OWNER);
    }

    // 이미 삭제된 메시지인지 확인
    if (chatMessage.getDeletedAt() != null) {
      throw new CustomException(MESSAGE_ALREADY_DELETED);
    }

    LocalDateTime deletedAt = LocalDateTime.now(ZoneOffset.UTC);

    chatMessage.delete(deletedAt);
    redisChatMessageRepository.deleteMessage(chatRoomId, messageId, deletedAt);
  }

//  /**
//   * 채팅 메시지 조회
//   */
//  @Transactional(readOnly = true)
//  public List<ChatMessageResDto> getChatRoomMessages(Long userId, Long chatRoomId, Long cursorEpochMs, int pageSize) {
//
//    ChatParticipant chatParticipant = getChatParticipant(chatRoomId, userId);
//
//    if (chatParticipant.isLeft()) {
//      throw new CustomException(ALREADY_LEFT_CHAT_ROOM);
//    }
//
//    // 현재 시간 기준 계산
//    long now = System.currentTimeMillis();
//    long threeDaysAgo = now - ofDays(3).toMillis();
//
//    // 커서 값 설정 -> 클라이언트에서 전달한 경우 사용, 없으면 최신 메시지 기준
//    long cursor = (cursorEpochMs != null) ? cursorEpochMs : MAX_VALUE;
//
//    // 최신 3일 메시지이면 redis 조회
//    if (cursor >= threeDaysAgo) {
//      return redisChatMessageRepository.findRecentMessage(chatRoomId, cursor, pageSize);
//    }
//
//    // 3일 이전 메시지이면 DB 조회
//    LocalDateTime cursorDateTime = LocalDateTime.ofInstant(Instant.ofEpochMilli(cursor), ZoneOffset.UTC);
//
//    List<ChatMessage> dbMessages = chatMessageRepository.findOldMessages(chatRoomId, cursorDateTime, pageSize);
//
//    return dbMessages.stream().map(ChatMessageResDto::from).toList();
//  }

  /**
   * 채팅 메세지 조회(최근)
   */
  @Transactional(readOnly = true)
  public List<ChatMessageResDto> getChatRoomMessages(Long userId, Long chatRoomId, Long cursor, int pageSize) {

    ChatParticipant chatParticipant = getChatParticipant(chatRoomId, userId);

    // 채팅방 참여 여부 확인
    if (chatParticipant.isLeft()) {
      throw new CustomException(ALREADY_LEFT_CHAT_ROOM);
    }

    // 3일 전
    long now = System.currentTimeMillis();
    long threeDaysAgo = now - ofDays(3).toMillis();

    long targetCursor = (cursor != null) ? cursor : MAX_VALUE;

    // 3일보다 이전 메세지는 redis에 없으므로 빈 배열
    if (targetCursor < threeDaysAgo) {
      return List.of();
    }

    return redisChatMessageRepository.findRecentMessage(chatRoomId, targetCursor, pageSize);
  }

  /**
   * 채팅 메세지 더보기
   */
  @Transactional(readOnly = true)
  public List<ChatMessageResDto> loadMoreChatMessages(Long userId, Long chatRoomId, Long cursor, int dayRange, int pageSize) {

    ChatParticipant chatParticipant = getChatParticipant(chatRoomId, userId);

    // 채팅방 참여 여부 확인
    if (chatParticipant.isLeft()) {
      throw new CustomException(ALREADY_LEFT_CHAT_ROOM);
    }

    long targetCursor = (cursor != null) ? cursor : System.currentTimeMillis();

    // 커서 epoch 값을 local date time 변환
    LocalDateTime cursorTime = LocalDateTime.ofInstant(Instant.ofEpochMilli(targetCursor), ZoneOffset.UTC);

    // 조회 범위
    LocalDateTime from = cursorTime.minusDays(dayRange);

    List<ChatMessage> chatMessages = chatMessageRepository.findChatMessagesBetween(chatRoomId, from, cursorTime, pageSize);

    return chatMessages.stream().map(ChatMessageResDto::from).toList();
  }

  /**
   * 채팅 메시지 저장
   */
  @Transactional
  public ChatMessageResDto saveChatMessage(Long chatRoomId, Long userId, ChatMessageReqDto reqDto) {

    ChatMessage chatMessage = ChatMessage.create(chatRoomId, userId, reqDto.contentType(), reqDto.messageContent());
    ChatParticipant chatParticipant = getChatParticipant(chatRoomId, userId);

    if (chatParticipant.isLeft()) {
      throw new CustomException(ALREADY_LEFT_CHAT_ROOM);
    }

    // DB 저장
    ChatMessage saveMessage = chatMessageRepository.save(chatMessage);

    // Redis 저장
    redisChatMessageRepository.saveMessage(saveMessage);

    return ChatMessageResDto.from(saveMessage);
  }

  private ChatParticipant getChatParticipant(Long chatRoomId, Long userId) {

    return chatParticipantRepository.findByChatRoomIdAndUserId(chatRoomId, userId)
        .orElseThrow(() -> new CustomException(CHAT_PARTICIPANT_NOT_FOUND));
  }

  private ChatRoom getChatRoom(Long chatRoomId) {

    return chatRoomRepository.findById(chatRoomId).orElseThrow(() -> new CustomException(CHAT_ROOM_NOT_FOUND));
  }

  private void validateDirectChatRequest(Long userId, CreateChatRoomReqDto reqDto) {

    // 두 유저가 같은 사람인지 확인
    if (reqDto.receiverId().equals(userId)) {
      throw new CustomException(SELF_CHAT_NOT_ALLOWED);
    }

    // 상대가 존재하는 유저인지 확인
    if (userClient.getUserInfo(reqDto.receiverId()) == null) {
      throw new CustomException(USER_NOT_FOUND);
    }

    // 차단 여부 확인
    boolean isNotBlocked = blockClient.isNotBlocked(userId, reqDto.receiverId());

    if (!isNotBlocked) {
      throw new CustomException(BLOCKED_USER);
    }
  }

  private CreateChatRoomResDto handleExistingDirectChatRoom(ChatRoom chatRoom, Long userId, Long receiverId) {

    validateParticipantCount(chatRoom.getId());

    ChatParticipant me = getChatParticipant(chatRoom.getId(), userId);
    ChatParticipant receiver = getChatParticipant(chatRoom.getId(), receiverId);

    boolean meLeft = me.isLeft();
    boolean receiverLeft = receiver.isLeft();

    // 둘 다 방을 떠났으면 새 채팅방 생성
    if (meLeft && receiverLeft) {
      return createNewChatRoom(userId, receiverId);
    }

    // 한 명만 나간 경우 재참여 처리
    if (meLeft) {
      me.join();
    }

    if (receiverLeft) {
      receiver.join();
    }

    return CreateChatRoomResDto.from(chatRoom);
  }

  private void validateParticipantCount(Long chatRoomId) {

    int count = chatParticipantRepository.countParticipants(chatRoomId);

    if (count != 2) {
      throw new CustomException(CHATROOM_PARTICIPANT_LIMIT_EXCEEDED);
    }
  }

  private CreateChatRoomResDto createNewChatRoom(Long userId, Long receiverId) {

    // 팔로우 여부 확인
    boolean isFollowing = followClient.isFollowing(userId, receiverId);

    // 한쪽이라도 팔로우 중이면 바로 OPEN
    ChatRoomStatus chatRoomStatus = isFollowing ? OPEN : REQUESTED;

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

//  /**
//   * 읽음 확인
//   */
//  @Transactional
//  public void markAsRead(ChatMessageReadReqDto reqDto) {
//
//    boolean alreadyReads = chatMessageReadRepository.findByChatMessageIdAndUserId(reqDto.chatMessageId(), reqDto.userId()).isPresent();
//
//    // 이미 읽은 메시지는 무시
//    if (alreadyReads) {
//      return;
//    }
//
//    chatMessageReadRepository.save(ChatMessageRead.create(reqDto));
//  }

// TODO:  읽음 확인

}