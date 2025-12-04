package com.monitor_service.presentation.controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.common.response.CommonResponse;
import com.common.response.SuccessCode;
import com.monitor_service.application.service.ReportService;
import com.monitor_service.presentation.request.CreateReportRequest;
import com.monitor_service.presentation.response.CreateReportResponse;

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
}
