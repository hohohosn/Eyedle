package com.eyedle.comment_service.domain.vo;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class NotificationReceivers {

	private final Set<Long> receiverIds = new HashSet<>();

	public void add(Long userId){
		if (receiverIds.contains(userId))
			return;
		receiverIds.add(userId);
	}

	public void remove(Long userId){
		receiverIds.remove(userId);
	}

	public boolean isEmpty(){
		return receiverIds.isEmpty();
	}

	public List<Long> toList() {
		return List.copyOf(receiverIds);
	}

}
