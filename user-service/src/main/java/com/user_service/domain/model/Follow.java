package com.user_service.domain.model;

import jakarta.persistence.*;
import lombok.*;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

import com.user_service.infrastructure.exception.BusinessException;
import com.user_service.infrastructure.exception.ErrorCode;

@Entity
@Table(name = "follows", indexes = {
	@Index(name = "idx_following_id", columnList = "following_id"),
	@Index(name = "idx_created_at", columnList = "created_at")
})
@EntityListeners(AuditingEntityListener.class)
@IdClass(FollowId.class)  // 복합 PK 지정
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@EqualsAndHashCode(of = {"followerId", "followingId"})
public class Follow {

	/**
	 * 팔로워
	 */
	@Id
	@Column(name = "follower_id", nullable = false)
	private Long followerId;

	/**
	 * 팔로잉
	 */
	@Id
	@Column(name = "following_id", nullable = false)
	private Long followingId;

	/**
	 * 팔로우 시작 시간
	 */
	@CreatedDate
	@Column(nullable = false, updatable = false)
	private LocalDateTime createdAt;

	/**
	 * 팔로우 관계 생성
	 */
	public static Follow create(Long followerId, Long followingId) {
		if (followerId.equals(followingId)) {
			throw new BusinessException(ErrorCode.CANNOT_FOLLOW_YOURSELF);
		}

		return Follow.builder()
			.followerId(followerId)
			.followingId(followingId)
			.build();
	}
}