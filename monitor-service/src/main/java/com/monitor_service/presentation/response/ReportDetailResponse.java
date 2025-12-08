package com.monitor_service.presentation.response;

import java.time.LocalDateTime;

import com.monitor_service.domain.model.Report;
import com.monitor_service.domain.model.ReportReason;
import com.monitor_service.domain.model.ReportStatus;
import com.monitor_service.domain.model.ReportTargetType;

public record ReportDetailResponse(
	Long reportId,
	Long reporterId,
	ReportTargetType targetType,
	Long targetId,
	ReportReason reason,
	String description,
	ReportStatus status,
	Long processorId,
	LocalDateTime processedAt,
	LocalDateTime createdAt,
	LocalDateTime updatedAt
) {
	public static ReportDetailResponse from(Report report) {
		return new ReportDetailResponse(
			report.getId(),
			report.getReporterId(),
			report.getTargetType(),
			report.getTargetId(),
			report.getReason(),
			report.getDescription(),
			report.getStatus(),
			report.getProcessorId(),
			report.getProcessedAt(),
			report.getCreatedAt(),
			report.getUpdatedAt()
		);
	}
}
