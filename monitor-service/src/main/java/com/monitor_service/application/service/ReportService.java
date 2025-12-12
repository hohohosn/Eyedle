package com.monitor_service.application.service;

import java.util.NoSuchElementException;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.common.exception.CustomException;
import com.common.response.ErrorCode;
import com.monitor_service.domain.model.Report;
import com.monitor_service.domain.model.ReportStatus;
import com.monitor_service.domain.repository.ReportRepository;
import com.monitor_service.presentation.request.CreateReportRequest;
import com.monitor_service.presentation.request.ProcessReportRequest;
import com.monitor_service.presentation.response.ReportDetailResponse;
import com.monitor_service.presentation.response.ReportResponse;

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

	public Page<ReportResponse> getReports(ReportStatus status, Pageable pageable){
		Page<Report> reports;

		if (status == null){
			reports = reportRepository.findAll(pageable);
		}else {
			reports = reportRepository.findAllByStatus(status, pageable);
		}

		return reports.map(ReportResponse::from);
	}

	public ReportDetailResponse getReport(Long reportId) {
		Report report = reportRepository.findById(reportId)
			.orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND));

		return ReportDetailResponse.from(report);
	}

	@Transactional
	public Long processReport(Long reportId, ProcessReportRequest request) {
		Report report = reportRepository.findById(reportId)
			.orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND));

		report.processReport(request.processorId(), request.status());

		return report.getId();
	}

	@Transactional
	public Long deleteReport(Long reportId) {
		Report report = reportRepository.findById(reportId)
			.orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND));

		report.deleteReport();

		return report.getId();
	}

}
