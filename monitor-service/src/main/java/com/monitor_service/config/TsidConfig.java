package com.monitor_service.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import com.common.utils.TsidUtil;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Configuration
public class TsidConfig {

	@Value("${instance.node-id}")
	private int nodeId;

	@PostConstruct
	public void init(){
		TsidUtil.setNodeId(nodeId);
		log.info(">>> TSID Node ID initialized: {}", nodeId);
	}
}
