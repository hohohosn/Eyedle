package com.eyedle.notification_service.presentation.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.http.MediaType;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import com.common.response.CommonResponse;
import com.common.response.SuccessCode;
import com.eyedle.notification_service.application.service.NotificationService;
import com.eyedle.notification_service.global.annotation.CurrentUser;
import com.eyedle.notification_service.global.dto.UserContext;
import com.eyedle.notification_service.presentation.dto.request.NotificationCreateRequestDto;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class NotificationController {

	private final NotificationService notificationService;

	@GetMapping(value = "/notifications/subscribe", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
	public SseEmitter subscribe(@CurrentUser UserContext user) {
		return notificationService.subscribe(user.getId());
	}

	@PostMapping("/internal/notifications")
	public CommonResponse<SuccessCode> sendNotification(@Valid @RequestBody NotificationCreateRequestDto notificationCreateRequestDto) {
		notificationService.sendNotification(notificationCreateRequestDto);
		return CommonResponse.of(SuccessCode.CREATED);
	}

}
