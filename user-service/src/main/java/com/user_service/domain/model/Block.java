package com.user_service.domain.model;

import jakarta.persistence.*;
import lombok.*;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

import com.user_service.infrastructure.exception.BusinessException;
import com.user_service.infrastructure.exception.ErrorCode;

@Entity
@Table(name = "blocks", indexes = {
	@Index(name = "idx_blocked_id", columnList = "blocked_id"),
	@Index(name = "idx_created_at", columnList = "created_at")
})
@EntityListeners(AuditingEntityListener.class)
@IdClass(BlockId.class)  // 복합 PK 지정
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@EqualsAndHashCode(of = {"blockerId", "blockedId"})
public class Block {

	/**
	 * 차단한 사용자 ID
	 */
	@Id
	@Column(name = "blocker_id", nullable = false)
	private Long blockerId;

	/**
	 * 차단당한 사용자 ID
	 */
	@Id
	@Column(name = "blocked_id", nullable = false)
	private Long blockedId;

	/**
	 * 차단 시작 시간
	 */
	@CreatedDate
	@Column(nullable = false, updatable = false)
	private LocalDateTime createdAt;

	/**
	 * 차단 관계 생성
	 */
	public static Block create(Long blockerId, Long blockedId) {
		if (blockerId.equals(blockedId)) {
			throw new BusinessException(ErrorCode.CANNOT_BLOCK_YOURSELF);
		}

		return Block.builder()
			.blockerId(blockerId)
			.blockedId(blockedId)
			.build();
	}
}