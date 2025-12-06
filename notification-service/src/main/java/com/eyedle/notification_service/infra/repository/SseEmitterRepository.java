package com.eyedle.notification_service.infra.repository;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.stereotype.Repository;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@Repository
public class SseEmitterRepository {

	private final Map<Long, SseEmitter> emitters = new ConcurrentHashMap<>();

	public void save(Long id, SseEmitter emitter) {
		emitters.put(id, emitter);
	}

	public SseEmitter get(Long id) {
		return emitters.get(id);
	}

	public SseEmitter deleteById(Long id) {
		return emitters.remove(id);
	}

}
