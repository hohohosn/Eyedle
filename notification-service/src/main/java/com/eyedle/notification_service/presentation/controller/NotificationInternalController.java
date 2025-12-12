package com.eyedle.notification_service.presentation.controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.common.response.CommonResponse;
import com.common.response.SuccessCode;
import com.eyedle.notification_service.application.service.NotificationService;
import com.eyedle.notification_service.presentation.dto.request.NotificationCreateRequestDto;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/internal/notifications")
@RequiredArgsConstructor
public class NotificationInternalController {

	private final NotificationService notificationService;

	/**
	 * 알림 발송
	 * @param notificationCreateRequestDto
	 * @return
	 */
	@PostMapping
	public CommonResponse<SuccessCode> sendNotification(@Valid @RequestBody NotificationCreateRequestDto notificationCreateRequestDto) {
		notificationService.sendNotification(notificationCreateRequestDto);
		return CommonResponse.of(SuccessCode.CREATED);
	}
}
