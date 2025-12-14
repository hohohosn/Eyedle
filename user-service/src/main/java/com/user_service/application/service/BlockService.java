package com.user_service.application.service;

import com.user_service.domain.model.Block;
import com.user_service.domain.model.User;
import com.user_service.domain.repository.BlockRepository;
import com.user_service.domain.repository.UserRepository;
import com.user_service.infrastructure.exception.BusinessException;
import com.user_service.infrastructure.exception.ErrorCode;
import com.user_service.presentation.dto.response.BlockResponse;
import com.user_service.presentation.dto.response.UserInternalResponse;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BlockService {

	private final BlockRepository blockRepository;
	private final UserRepository userRepository;

	/**
	 * 사용자 차단
	 */
	@Transactional
	public BlockResponse blockUser(Long blockerId, Long blockedId) {
		log.info("사용자 차단 시도: blockerId={}, blockedId={}", blockerId, blockedId);

		// 차단 대상 사용자 존재 확인
		userRepository.findByIdAndNotDeleted(blockedId)
			.orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

		// 이미 차단 중인지 확인
		if (blockRepository.existsByBlockerIdAndBlockedId(blockerId, blockedId)) {
			throw new BusinessException(ErrorCode.ALREADY_BLOCKED);
		}

		// 차단 관계 생성
		Block block = Block.create(blockerId, blockedId);
		blockRepository.save(block);

		log.info("사용자 차단 완료: blockerId={}, blockedId={}", blockerId, blockedId);

		// 응답 생성
		return BlockResponse.builder()
			.blockerId(blockerId)
			.blockedId(blockedId)
			.isBlocked(true)
			.createdAt(block.getCreatedAt())
			.build();
	}

	/**
	 * 차단 해제
	 */
	@Transactional
	public void unblockUser(Long blockerId, Long blockedId) {
		log.info("차단 해제 시도: blockerId={}, blockedId={}", blockerId, blockedId);

		// 차단 관계 확인
		if (!blockRepository.existsByBlockerIdAndBlockedId(blockerId, blockedId)) {
			throw new BusinessException(ErrorCode.NOT_BLOCKED);
		}

		// 차단 관계 삭제
		blockRepository.deleteByBlockerIdAndBlockedId(blockerId, blockedId);

		log.info("차단 해제 완료: blockerId={}, blockedId={}", blockerId, blockedId);
	}

	/**
	 * 차단 여부 확인
	 */
	public boolean isBlocked(Long blockerId, Long blockedId) {
		return blockRepository.existsByBlockerIdAndBlockedId(blockerId, blockedId);
	}

	/**
	 * 양방향 차단 여부 확인 (A가 B를 차단했거나 B가 A를 차단한 경우)
	 */
	public boolean isEitherBlocked(Long userId1, Long userId2) {
		return blockRepository.isEitherBlocked(userId1, userId2);
	}

	/**
	 * 내가 차단한 사용자 목록 조회
	 */
	public Slice<UserInternalResponse> getBlockedUsers(Long userId, Pageable pageable) {
		log.info("차단한 사용자 목록 조회: userId={}", userId);

		// 차단한 사용자 ID 목록 조회
		Slice<Long> blockedIds = blockRepository.findBlockedIds(userId, pageable);

		// 유저 정보 일괄 조회
		List<Long> ids = blockedIds.getContent();
		Map<Long, User> userMap = userRepository.findAllById(ids).stream()
			.collect(Collectors.toMap(User::getId, user -> user));

		// 응답 생성 (원본 순서 유지)
		List<UserInternalResponse> users = ids.stream()
			.map(id -> {
				User user = userMap.get(id);
				if (user == null) {
					log.warn("차단한 사용자 정보를 찾을 수 없음: blockedId={}", id);
					return null;
				}
				return UserInternalResponse.from(user);
			})
			.filter(user -> user != null)
			.collect(Collectors.toList());

		return new SliceImpl<>(users, pageable, blockedIds.hasNext());
	}

	/**
	 * 여러 사용자에 대한 차단 여부 일괄 조회 (Internal API용)
	 */
	public Map<Long, Boolean> checkBlockedBatch(Long blockerId, List<Long> targetUserIds) {
		log.info("차단 여부 일괄 조회: blockerId={}, targetCount={}", blockerId, targetUserIds.size());

		List<Long> blockedIds = blockRepository.findBlockedIdsByBlockerIdAndBlockedIdIn(
			blockerId, targetUserIds
		);

		return targetUserIds.stream()
			.collect(Collectors.toMap(
				id -> id,
				blockedIds::contains
			));
	}
}