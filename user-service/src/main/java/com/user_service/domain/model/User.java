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
	@Index(name = "idx_status", columnList = "status"),
	@Index(name = "idx_deleted_at", columnList = "deleted_at")
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

	// 논리적 삭제 필드 (Nullable)
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
			.createdBy(createdBy)
			.build();
	}

	// 비즈니스 메서드

	/**
	 * 비밀번호 변경
	 */
	public void updatePassword(String newPassword, String updatedBy) {
		this.password = newPassword;
		this.updatedBy = updatedBy;
	}

	/**
	 * 프로필 수정
	 */
	public void updateProfile(String username, String updatedBy) {
		this.username = username;
		this.updatedBy = updatedBy;
	}

	/**
	 * 계정 활성화 여부 확인(deletedAt 필드로 판단)
	 */
	public boolean isActive() {
		return this.status == UserStatus.ACTIVE && this.deletedAt == null;
	}

	/**
	 * 삭제 여부 확인(deletedAt 필드로 판단)
	 */
	public boolean isDeleted() {
		return this.deletedAt != null;
	}

	/**
	 * 계정 상태 변경
	 */
	public void updateStatus(UserStatus newStatus, String updatedBy) {
		this.status = newStatus;
		this.updatedBy = updatedBy;
	}

	/**
	 * 회원 탈퇴 (Soft Delete)
	 */
	public void softDelete(String deletedBy) {
		this.status = UserStatus.DELETED;
		this.deletedAt = LocalDateTime.now();
		this.deletedBy = deletedBy;
	}
}