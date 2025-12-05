package com.monitor_service.presentation.controller;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.common.response.CommonResponse;
import com.common.response.SuccessCode;
import com.monitor_service.application.service.ReportService;
import com.monitor_service.domain.model.ReportStatus;
import com.monitor_service.presentation.request.CreateReportRequest;
import com.monitor_service.presentation.request.ProcessReportRequest;
import com.monitor_service.presentation.response.CreateReportResponse;
import com.monitor_service.presentation.response.ReportDetailResponse;
import com.monitor_service.presentation.response.ReportResponse;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/monitor/reports")
public class ReportController {

	private final ReportService reportService;

	@PostMapping
	public CommonResponse<CreateReportResponse> createReport(@RequestBody CreateReportRequest request){
		Long reportId = reportService.createReport(request);

		CreateReportResponse response = new CreateReportResponse(reportId);

		return CommonResponse.of(SuccessCode.CREATED, response);
	}

	@GetMapping
	public CommonResponse<Page<ReportResponse>> getReports(
		@RequestParam(required = false)ReportStatus status,
		@PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable
	){
		Page<ReportResponse> response = reportService.getReports(status, pageable);
		return CommonResponse.of(SuccessCode.OK, response);
	}

	@GetMapping("/{reportId}")
	public CommonResponse<ReportDetailResponse> getReport(@PathVariable Long reportId) {
		ReportDetailResponse response = reportService.getReport(reportId);
		return CommonResponse.of(SuccessCode.OK, response);
	}

	@PutMapping("/{reportId}")
	public CommonResponse<Long> processReport(
		@PathVariable Long reportId,
		@RequestBody ProcessReportRequest request
	){
		Long processedReportId = reportService.processReport(reportId, request);

		return CommonResponse.of(SuccessCode.UPDATED, processedReportId);
	}
}
