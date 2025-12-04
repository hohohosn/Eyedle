package com.monitor_service.presentation.request;

import com.monitor_service.domain.model.ReportReason;
import com.monitor_service.domain.model.ReportTargetType;

public record CreateReportRequest(
	Long reporterId,
	ReportTargetType targetType,
	Long targetId,
	ReportReason reason,
	String description
) {
}
