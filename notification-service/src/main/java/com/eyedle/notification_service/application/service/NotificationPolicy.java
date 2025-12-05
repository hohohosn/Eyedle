package com.eyedle.notification_service.application.service;

import org.springframework.stereotype.Component;

import com.common.exception.CustomException;
import com.eyedle.notification_service.presentation.enums.NotificationErrorCode;

@Component
public class NotificationPolicy {

	public void UserIsReceiver(Long userId, Long receiverId){
		if (!userId.equals(receiverId)){
			throw new CustomException(NotificationErrorCode.NOTIFICATION_FORBIDDEN);
		}
	}
}
