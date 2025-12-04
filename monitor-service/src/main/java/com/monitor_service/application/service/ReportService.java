package com.monitor_service.application.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.monitor_service.domain.model.Report;
import com.monitor_service.domain.repository.ReportRepository;
import com.monitor_service.presentation.request.CreateReportRequest;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ReportService {

	private final ReportRepository reportRepository;

	@Transactional
	public Long createReport(CreateReportRequest request) {
		// DTO -> entity로 변환
		Report report = Report.builder()
			.reporterId(request.reporterId())
			.targetType(request.targetType())
			.targetId(request.targetId())
			.reason(request.reason())
			.description(request.description())
			.build();

		Report savedReport = reportRepository.save(report);

		return savedReport.getId();
	}
}
