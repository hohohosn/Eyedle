package com.user_service.domain.model;

import io.hypersistence.tsid.TSID;
import jakarta.persistence.*;
import lombok.*;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
@Table(name = "users", indexes = {
	@Index(name = "idx_email", columnList = "email"),
	@Index(name = "idx_username", columnList = "username"),
	@Index(name = "idx_status", columnList = "status")
})
@EntityListeners(AuditingEntityListener.class)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@EqualsAndHashCode(of = "id")
public class User {

	@Id
	@Column(name = "id", nullable = false, updatable = false)
	private Long id;  // TSID

	@Column(nullable = false, unique = true, length = 100)
	private String email;

	@Column(nullable = false, unique = true, length = 50)
	private String username;

	@Column(nullable = false)
	private String password;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false, length = 20)
	@Builder.Default
	private UserRole role = UserRole.USER;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false, length = 20)
	@Builder.Default
	private UserStatus status = UserStatus.ACTIVE;

	@Column(nullable = false)
	@Builder.Default
	private boolean deleted = false;

	// Auditing 필드
	@CreatedDate
	@Column(nullable = false, updatable = false)
	private LocalDateTime createdAt;

	@Column(updatable = false, length = 50)
	private String createdBy;

	@LastModifiedDate
	@Column(nullable = false)
	private LocalDateTime updatedAt;

	@Column(length = 50)
	private String updatedBy;

	@Column
	private LocalDateTime deletedAt;

	@Column(length = 50)
	private String deletedBy;

	// TSID 자동 생성
	@PrePersist
	public void prePersist() {
		if (this.id == null) {
			this.id = TSID.fast().toLong();
		}
	}

	// 정적 팩토리 메서드

	public static User createUser(
		String email,
		String username,
		String encodedPassword,
		String createdBy
	) {
		return User.builder()
			.email(email)
			.username(username)
			.password(encodedPassword)
			.role(UserRole.USER)
			.status(UserStatus.ACTIVE)
			.deleted(false)
			.createdBy(createdBy)
			.build();
	}

	public static User createAdmin(
		String email,
		String username,
		String encodedPassword,
		String createdBy
	) {
		return User.builder()
			.email(email)
			.username(username)
			.password(encodedPassword)
			.role(UserRole.ADMIN)
			.status(UserStatus.ACTIVE)
			.deleted(false)
			.createdBy(createdBy)
			.build();
	}

	// 비즈니스 메서드

	/**
	 * 비밀번호 변경
	 */
	public void updatePassword(String encodedPassword, String updatedBy) {
		this.password = encodedPassword;
		this.updatedBy = updatedBy;
	}

	/**
	 * 프로필 수정
	 */
	public void updateProfile(String username, String updatedBy) {
		if (username != null && !username.isBlank()) {
			this.username = username;
		}
		this.updatedBy = updatedBy;
	}

	/**
	 * 회원 탈퇴 (Soft Delete)
	 */
	public void softDelete(String deletedBy) {
		this.deleted = true;
		this.status = UserStatus.DELETED;
		this.deletedAt = LocalDateTime.now();
		this.deletedBy = deletedBy;
	}

	/**
	 * 회원 복구
	 */
	public void restore(String updatedBy) {
		this.deleted = false;
		this.status = UserStatus.ACTIVE;
		this.deletedAt = null;
		this.deletedBy = null;
		this.updatedBy = updatedBy;
	}

	/**
	 * 권한 변경 (관리자용)
	 */
	public void changeRole(UserRole role, String updatedBy) {
		this.role = role;
		this.updatedBy = updatedBy;
	}

	/**
	 * 계정 상태 변경
	 */
	public void changeStatus(UserStatus status, String updatedBy) {
		this.status = status;
		this.updatedBy = updatedBy;
	}

	/**
	 * 계정 활성화 여부
	 */
	public boolean isActive() {
		return this.status == UserStatus.ACTIVE && !this.deleted;
	}

	/**
	 * 삭제된 계정인지 확인
	 */
	public boolean deleted() {
		return this.deleted;
	}
}