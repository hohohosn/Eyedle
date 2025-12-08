package com.monitor_service.presentation.request;

import com.monitor_service.domain.model.ReportStatus;

public record ProcessReportRequest(
	ReportStatus status,
	Long processorId
) {
}
