package com.eyedle.notification_service.domain.service;

import org.springframework.stereotype.Component;

import com.common.exception.CustomException;
import com.eyedle.notification_service.presentation.enums.NotificationErrorCode;

@Component
public class NotificationPolicy {

	public void userIsReceiver(Long userId, Long receiverId){
		if (!userId.equals(receiverId)){
			throw new CustomException(NotificationErrorCode.NOTIFICATION_FORBIDDEN);
		}
	}
}
