package com.eyedle.notification_service.presentation.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.http.MediaType;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import com.eyedle.notification_service.application.service.NotificationService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class NotificationController {

	private final NotificationService notificationService;
	private static Long USER_ID = 500L;

	@GetMapping(value = "/internal/subscribe", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
	public SseEmitter subscribe(/*@CurrentUser UserContext user*/) {
		return notificationService.subscribe(500L);
	}

}
