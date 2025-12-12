package com.monitor_service.domain.model;

import java.time.LocalDateTime;

import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

import com.common.database.BaseTimeEntity;
import com.common.exception.CustomException;
import com.common.response.ErrorCode;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "p_reports")
@SQLDelete(sql = "UPDATE p_reports SET deleted_at = NOW() WHERE id = ?")
@SQLRestriction("deleted_at IS NULL")
public class Report extends BaseTimeEntity{

	@Column(nullable = false)
	private Long reporterId;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private ReportTargetType targetType;

	@Column(nullable = false)
	private Long targetId;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private ReportReason reason;

	@Column(columnDefinition = "TEXT")
	private String description;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private ReportStatus status;

	@Column
	private Long processorId;

	@Column
	private LocalDateTime processedAt;

	@Column
	private LocalDateTime deletedAt;

	@Builder
	public Report(Long reporterId, ReportTargetType targetType, Long targetId, ReportReason reason, String description){
		this.reporterId = reporterId;
		this.targetType = targetType;
		this.targetId = targetId;
		this.reason = reason;
		this.description = description;
		this.status = ReportStatus.PENDING;
	}

	public void processReport(Long processorId, ReportStatus newStatus){
		if (newStatus == ReportStatus.PENDING) {
			throw new CustomException(ErrorCode.BAD_REQUEST);
		}

		this.processorId = processorId;
		this.status = newStatus;
		this.processedAt = LocalDateTime.now();
	}

	public void deleteReport() {
		this.deletedAt = LocalDateTime.now();
	}
}
