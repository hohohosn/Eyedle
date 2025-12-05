package com.eyedle.notification_service.presentation.controller;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.http.MediaType;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import com.common.response.CommonResponse;
import com.common.response.SuccessCode;
import com.eyedle.notification_service.application.service.NotificationService;
import com.eyedle.notification_service.global.annotation.CurrentUser;
import com.eyedle.notification_service.global.dto.UserContext;
import com.eyedle.notification_service.presentation.dto.SliceResponse;
import com.eyedle.notification_service.presentation.dto.request.NotificationCreateRequestDto;
import com.eyedle.notification_service.presentation.dto.response.NotificationGetResponseDto;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class NotificationController {

	private final NotificationService notificationService;

	/**
	 * SSE 연결
	 * @param user
	 * @return
	 */
	@GetMapping(value = "/notifications/subscribe", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
	public SseEmitter subscribe(@CurrentUser UserContext user) {
		return notificationService.subscribe(user.getId());
	}

	/**
	 * 알림 발송
	 * @param notificationCreateRequestDto
	 * @return
	 */
	@PostMapping("/internal/notifications")
	public CommonResponse<SuccessCode> sendNotification(@Valid @RequestBody NotificationCreateRequestDto notificationCreateRequestDto) {
		notificationService.sendNotification(notificationCreateRequestDto);
		return CommonResponse.of(SuccessCode.CREATED);
	}

	/**
	 * 내 알림 목록 조회
	 * @param user
	 * @param cursor
	 * @param size
	 * @return
	 */
	@GetMapping("/notifications")
	public CommonResponse<SliceResponse<NotificationGetResponseDto>> getNotifications(@CurrentUser UserContext user,
		@RequestParam(required = false) Long cursor,
		@RequestParam(defaultValue = "20") int size
	){
		SliceResponse<NotificationGetResponseDto> result = notificationService.getNotifications(user.getId(), cursor, size);
		return CommonResponse.of(SuccessCode.OK, result);
	}

	/**
	 * 읽지 않은 알림 전체 읽음
	 * @param user
	 * @return
	 */
	@PatchMapping("/notifications")
	public CommonResponse<SuccessCode> readAllNotifications(@CurrentUser UserContext user) {
		notificationService.readAllNotifications(user.getId());
		return CommonResponse.of(SuccessCode.UPDATED);
	}

	/**
	 * 알림 단건 읽음
	 * @param user
	 * @param notificationId
	 * @return
	 */
	@PatchMapping("/notifications/{notificationId}")
	public CommonResponse<SuccessCode> readNotification(@CurrentUser UserContext user,
		@PathVariable("notificationId") Long notificationId) {
		notificationService.readNotification(user.getId(), notificationId);
		return CommonResponse.of(SuccessCode.UPDATED);
	}




}
