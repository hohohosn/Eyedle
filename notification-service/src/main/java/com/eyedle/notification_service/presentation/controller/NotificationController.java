package com.eyedle.notification_service.presentation.controller;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import com.common.response.CommonResponse;
import com.common.response.SuccessCode;
import com.eyedle.notification_service.application.service.NotificationService;
import com.eyedle.notification_service.global.annotation.CurrentUser;
import com.eyedle.notification_service.global.dto.UserContext;
import com.eyedle.notification_service.presentation.dto.SliceResponse;
import com.eyedle.notification_service.presentation.dto.response.NotificationCountsResponseDto;
import com.eyedle.notification_service.presentation.dto.response.NotificationGetResponseDto;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/notifications")
public class NotificationController {

	private final NotificationService notificationService;

	/**
	 * SSE 연결
	 * @param userId
	 * @return
	 */
	@GetMapping(value = "/subscribe", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
	public SseEmitter subscribe(@RequestHeader("X-User-Id") Long userId) {
		return notificationService.subscribe(userId);
	}

	/**
	 * 내 알림 목록 조회
	 * @param userId
	 * @param cursor
	 * @param size
	 * @return
	 */
	@GetMapping
	public CommonResponse<SliceResponse<NotificationGetResponseDto>> getNotifications(@RequestHeader("X-User-Id") Long userId,
		@RequestParam(required = false) Long cursor,
		@RequestParam(defaultValue = "20") int size
	){
		SliceResponse<NotificationGetResponseDto> result = notificationService.getNotifications(userId, cursor, size);
		return CommonResponse.of(SuccessCode.OK, result);
	}

	/**
	 * 읽지 않은 알림 전체 읽음
	 * @param userId
	 * @return
	 */
	@PatchMapping
	public CommonResponse<NotificationCountsResponseDto> readAllNotifications(@RequestHeader("X-User-Id") Long userId) {
		NotificationCountsResponseDto notificationCountsResponseDto = notificationService.readAllNotifications(userId);
		return CommonResponse.of(SuccessCode.OK, notificationCountsResponseDto);
	}

	/**
	 * 알림 일괄삭제
	 * @param userId
	 * @return
	 */
	@DeleteMapping
	public CommonResponse<NotificationCountsResponseDto> deleteAllNotifications(@RequestHeader("X-User-Id") Long userId) {
		NotificationCountsResponseDto notificationCountsResponseDto = notificationService.deleteAllNotifications(userId);
		return CommonResponse.of(SuccessCode.OK, notificationCountsResponseDto);
	}

	/**
	 * 알림 단건 읽음
	 * @param userId
	 * @param notificationId
	 * @return
	 */
	@PatchMapping("/{notificationId}")
	public CommonResponse<SuccessCode> readNotification(@RequestHeader("X-User-Id") Long userId,
		@PathVariable("notificationId") Long notificationId) {
		notificationService.readNotification(userId, notificationId);
		return CommonResponse.of(SuccessCode.UPDATED);
	}

	/**
	 * 알림 단건 삭제
	 * @param userId
	 * @param notificationId
	 * @return
	 */
	@DeleteMapping("/{notificationId}")
	public CommonResponse<SuccessCode> deleteNotification(@RequestHeader("X-User-Id") Long userId,
		@PathVariable("notificationId") Long notificationId) {
		notificationService.deleteNotification(userId, notificationId);
		return CommonResponse.of(SuccessCode.DELETED);
	}

	/**
	 *
	 * @param userId
	 * @return
	 */
	@GetMapping("/counts")
	public CommonResponse<NotificationCountsResponseDto> countUnreadNotifications(@RequestHeader("X-User-Id") Long userId){
		NotificationCountsResponseDto notificationCountsResponseDto = notificationService.countUnreadNotifications(userId);
		return CommonResponse.of(SuccessCode.OK, notificationCountsResponseDto);
	}


}
