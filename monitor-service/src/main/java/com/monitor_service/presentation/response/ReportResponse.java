package com.monitor_service.presentation.response;

import java.time.LocalDateTime;

import com.monitor_service.domain.model.Report;
import com.monitor_service.domain.model.ReportReason;
import com.monitor_service.domain.model.ReportStatus;
import com.monitor_service.domain.model.ReportTargetType;

public record ReportResponse(
	Long reportId,
	Long reporterId,
	ReportTargetType targetType,
	Long targetId,
	ReportReason reason,
	ReportStatus status,
	LocalDateTime createdAt
) {
	// 엔티티에서 dto로 변환
	public static ReportResponse from(Report report){
		return new ReportResponse(
			report.getId(),
			report.getReporterId(),
			report.getTargetType(),
			report.getTargetId(),
			report.getReason(),
			report.getStatus(),
			report.getCreatedAt()
		);
	}
}
