package com.eyedle.notification_service.domain.model;

public enum NotificationType {
	LIKED("님이 좋아합니다"),
	FOLLOWED("님이 팔로우합니다."),
	FOLLOW_APPROVED("님이 팔로우를 승인했습니다."),
	COMMENT_REPLY("님이 댓글에 답글을 작성했습니다."),
	FEED_COMMENT("님이 게시글에 댓글을 작성했습니다."),
	NOTICE("");

	private final String messageTemplate;

	NotificationType(String messageTemplate) {
		this.messageTemplate = messageTemplate;
	}

	public String getMessageTemplate(String sender, String message) {

		if (this == NOTICE){
			return message;
		}
		return String.format("%s%s",sender, messageTemplate);
	}
}
