package com.eyedle.comment_service.application.dto.message;

import java.util.List;

import com.eyedle.comment_service.domain.model.Comment;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NotificationEventDto {

	private List<Long> receiverIds;
	private Long targetId;
	private Long subTargetId;
	private String type;
	private String sender;

	public static NotificationEventDto toEvent(Comment comment
		, List<Long> receiverIds
		, String type) {

		Long subTargetId = comment.getId();

		if (comment.getParentId() != null) {
			subTargetId = comment.getParentId();
		}

		return NotificationEventDto.builder()
			.receiverIds(receiverIds)
			.type(type)
			.sender(comment.getAuthor().getName())
			.targetId(comment.getFeedId())
			.subTargetId(subTargetId)
			.build();

	}

}
