package com.user_service.application.service;

import com.user_service.domain.model.Follow;
import com.user_service.domain.model.User;
import com.user_service.domain.repository.FollowRepository;
import com.user_service.domain.repository.UserRepository;
import com.user_service.infrastructure.exception.BusinessException;
import com.user_service.infrastructure.exception.ErrorCode;
import com.user_service.presentation.dto.response.FollowResponse;
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
public class FollowService {

	private final FollowRepository followRepository;
	private final UserRepository userRepository;

	/**
	 * 팔로우
	 */
	@Transactional
	public FollowResponse follow(Long followerId, Long followingId) {
		log.info("팔로우 시도: followerId={}, followingId={}", followerId, followingId);

		// 자기 자신을 팔로우하는지 검증
		if (followerId.equals(followingId)) {
			throw new BusinessException(ErrorCode.CANNOT_FOLLOW_YOURSELF);
		}

		// 이미 팔로우 중인지 확인
		if (followRepository.existsByFollowerIdAndFollowingId(followerId, followingId)) {
			throw new BusinessException(ErrorCode.ALREADY_FOLLOWING);
		}

		//팔로우 관계 생성
		Follow follow = Follow.create(followerId, followingId);
		followRepository.save(follow);

		log.info("팔로우 완료: followerId={}, followingId={}", followerId, followingId);

		//응답 생성
		return FollowResponse.builder()
			.followerId(followerId)
			.followingId(followingId)
			.isFollowing(true)
			.createdAt(follow.getCreatedAt())
			.build();
	}

	/**
	 * 언팔로우
	 */
	@Transactional
	public void unfollow(Long followerId, Long followingId) {
		log.info("언팔로우 시도: followerId={}, followingId={}", followerId, followingId);

		// 팔로우 관계 확인
		if (!followRepository.existsByFollowerIdAndFollowingId(followerId, followingId)) {
			throw new BusinessException(ErrorCode.NOT_FOLLOWING);
		}

		// 팔로우 관계 삭제
		followRepository.deleteByFollowerIdAndFollowingId(followerId, followingId);

		log.info("언팔로우 완료: followerId={}, followingId={}", followerId, followingId);
	}

	/**
	 * 팔로우 여부 확인
	 */
	public boolean isFollowing(Long followerId, Long followingId) {
		return followRepository.existsByFollowerIdAndFollowingId(followerId, followingId);
	}

	/**
	 * 팔로워 목록 조회 (나를 팔로우하는 사람들)
	 */
	public Slice<UserInternalResponse> getFollowers(Long userId, Pageable pageable) {
		log.info("팔로워 목록 조회: userId={}", userId);

		// 팔로워 ID 목록 조회
		Slice<Long> followerIds = followRepository.findFollowerIds(userId, pageable);

		// 유저 정보 일괄 조회
		List<Long> ids = followerIds.getContent();
		Map<Long, User> userMap = userRepository.findAllById(ids)
			.stream()
			.collect(Collectors.toMap(User::getId, user -> user));

		// 응답 생성
		List<UserInternalResponse> users = ids.stream()
			.map(userMap::get)
			.filter(user -> user != null && !user.isDeleted())
			.map(UserInternalResponse::from)
			.collect(Collectors.toList());

		return new SliceImpl<>(users, pageable, followerIds.hasNext());
	}

	/**
	 * 팔로잉 목록 조회 (내가 팔로우하는 사람들)
	 */
	public Slice<UserInternalResponse> getFollowings(Long userId, Pageable pageable) {
		log.info("팔로잉 목록 조회: userId={}", userId);

		// 팔로잉 ID 목록 조회
		Slice<Long> followingIds = followRepository.findFollowingIds(userId, pageable);

		// 유저 정보 일괄 조회
		List<Long> ids = followingIds.getContent();
		Map<Long, User> userMap = userRepository.findAllById(ids)
			.stream()
			.collect(Collectors.toMap(User::getId, user -> user));

		// 응답 생성
		List<UserInternalResponse> users = ids.stream()
			.map(userMap::get)
			.filter(user -> user != null && !user.isDeleted())
			.map(UserInternalResponse::from)
			.collect(Collectors.toList());

		return new SliceImpl<>(users, pageable, followingIds.hasNext());
	}

	/**
	 * 팔로우 일괄 조회 (N+1 방지)
	 */
	public Map<Long, Boolean> checkFollowingBatch(Long followerId, List<Long> followingIds) {
		List<Long> followingList = followRepository
			.findFollowingIdsByFollowerIdAndFollowingIdIn(followerId, followingIds);

		return followingIds.stream()
			.collect(Collectors.toMap(
				id -> id,
				followingList::contains
			));
	}
}