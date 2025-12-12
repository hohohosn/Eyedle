package com.eyedle.notification_service.infra.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import com.common.utils.TsidUtil;

import jakarta.annotation.PostConstruct;

@Configuration
public class TsidConfig {

	@Value("${instance.node-id:0}")
	private int nodeId;

	@PostConstruct
	public void init() {
		TsidUtil.setNodeId(nodeId);
	}

}
