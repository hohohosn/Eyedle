package com.user_service.application.service;

import com.user_service.domain.model.User;
import com.user_service.domain.repository.UserRepository;
import com.user_service.infrastructure.exception.BusinessException;
import com.user_service.infrastructure.exception.ErrorCode;
import com.user_service.presentation.dto.request.UpdateUserRequest;
import com.user_service.presentation.dto.response.UserInfoResponse;
import com.user_service.presentation.dto.response.UserInternalResponse;
import com.user_service.presentation.dto.response.UserSearchResponse;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserService {

	private final UserRepository userRepository;
	private final PasswordEncoder passwordEncoder;

	/**
	 * 내 정보 조회
	 */
	public UserInfoResponse getMyInfo(Long userId) {
		log.info("사용자 정보 조회: userId={}", userId);

		User user = userRepository.findByIdAndNotDeleted(userId)
			.orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

		return UserInfoResponse.from(user);
	}

	/**
	 * 특정 사용자 조회 (관리자용 - 활성 회원만)
	 */
	public UserInfoResponse getUserInfo(Long targetUserId) {
		log.info("사용자 정보 조회 (관리자): targetUserId={}", targetUserId);

		User user = userRepository.findByIdAndNotDeleted(targetUserId)
			.orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

		return UserInfoResponse.from(user);
	}

	/**
	 * 특정 사용자 조회 (관리자용 - 탈퇴 포함)
	 */
	public UserInfoResponse getUserInfoIncludingDeleted(Long targetUserId) {
		log.info("사용자 정보 조회 (탈퇴 포함): targetUserId={}", targetUserId);

		User user = userRepository.findById(targetUserId)
			.orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

		return UserInfoResponse.from(user);
	}

	/**
	 * 사용자 검색 (관리용)
	 */
	public UserSearchResponse searchUsers(String keyword, Pageable pageable) {
		log.info("사용자 검색: keyword={}, page={}", keyword, pageable.getPageNumber());

		Page<User> userPage;

		if (keyword == null || keyword.isBlank()) {
			// 키워드가 없으면 전체 조회
			userPage = userRepository.findAll(pageable);
		} else {
			// 이메일 또는 사용자명으로 검색 (기존 searchByKeyword 활용)
			userPage = userRepository.searchByKeyword(keyword, pageable);
		}

		Page<UserInfoResponse> responsePage = userPage.map(UserInfoResponse::from);
		return UserSearchResponse.from(responsePage);
	}

	/**
	 * 내 정보 수정
	 */
	@Transactional
	public UserInfoResponse updateMyInfo(Long userId, UpdateUserRequest request) {

		User user = userRepository.findByIdAndNotDeleted(userId)
			.orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

		// Username 변경
		if (request.getUsername() != null) {
			// 중복 체크
			if (!user.getUsername().equals(request.getUsername()) &&
				userRepository.existsByUsername(request.getUsername())) {
				throw new BusinessException(ErrorCode.DUPLICATE_USERNAME);
			}
			user.updateProfile(request.getUsername());
			log.info("Username 변경: userId={}, newUsername={}", userId, request.getUsername());
		}

		// 비밀번호 변경
		if (request.getCurrentPassword() != null && request.getNewPassword() != null) {
			// 현재 비밀번호 검증
			if (!passwordEncoder.matches(request.getCurrentPassword(), user.getPassword())) {
				throw new BusinessException(ErrorCode.INVALID_PASSWORD);
			}

			// 새 비밀번호 암호화 및 저장
			String encodedNewPassword = passwordEncoder.encode(request.getNewPassword());
			user.updatePassword(encodedNewPassword);
			log.info("비밀번호 변경 완료: userId={}", userId);
		}

		User updatedUser = userRepository.save(user);
		log.info("사용자 정보 수정 완료: userId={}", userId);

		return UserInfoResponse.from(updatedUser);
	}

	/**
	 * 회원 탈퇴 (Soft Delete)
	 */
	@Transactional
	public void deleteMyAccount(Long userId) {

		User user = userRepository.findByIdAndNotDeleted(userId)
			.orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

		user.softDelete(String.valueOf(userId));
		userRepository.save(user);

		log.info("회원 탈퇴 완료: userId={}", userId);
	}

	/**
	 * 유저 일괄 조회 (Internal)
	 */
	public Map<Long, UserInternalResponse> getUsersByIds(List<Long> userIds) {
		log.info("유저 일괄 조회: count={}", userIds.size());

		if (userIds == null || userIds.isEmpty()) {
			return Map.of();
		}

		// 중복 제거 및 일괄 조회
		List<Long> distinctUserIds = userIds.stream().distinct().toList();
		List<User> users = userRepository.findAllById(distinctUserIds);

		// Map으로 변환(userId -> UserInternalResponse)
		return users.stream()
			.filter(user -> !user.isDeleted())
			.collect(Collectors.toMap(
				User::getId,
				UserInternalResponse::from
			));
	}

	/**
	 * 단일 유저 조회 (Internal)
	 */
	public UserInternalResponse getInternalUserInfo(Long userId) {
		log.info("내부 API 유저 정보 조회: userId={}", userId);

		User user = userRepository.findByIdAndNotDeleted(userId)
			.orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

		return UserInternalResponse.from(user);
	}
}