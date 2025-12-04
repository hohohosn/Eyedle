package com.monitor_service.domain.repository;

import com.monitor_service.domain.model.Report;

public interface ReportRepository {
	Report save(Report report);
}
