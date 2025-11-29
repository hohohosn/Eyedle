package com.eyedle.comment_service.infra.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.github.f4b6a3.tsid.TsidFactory;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Configuration
public class TsidConfig {

	// 환경변수나 yml에 "instance.node.id"가 없으면 기본값 0 사용
	@Value("${instance.node.id:0}")
	private int nodeId;

	@Bean
	public TsidFactory tsidFactory() {
		log.info("TSID Factory 초기화 중... 할당된 Node ID: {}", nodeId);

		if (nodeId < 0 || nodeId > 1023) {
			throw new IllegalStateException("Node ID는 0~1023 사이어야 합니다.");
		}

		return TsidFactory.builder()
			.withNode(nodeId)
			.build();
	}

	@Bean
	public Void initTsidHolder(TsidFactory tsidFactory) {
		TsidHolder.setFactory(tsidFactory);
		return null;
	}
}