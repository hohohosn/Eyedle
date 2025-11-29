package com.eyedle.comment_service.infra.config;

import com.github.f4b6a3.tsid.TsidFactory;

public class TsidHolder {
	private static TsidFactory tsidFactory = TsidFactory.builder()
														.withNode(0)
														.build();


	public static void setFactory(TsidFactory factory) {
		tsidFactory = factory;
	}

	public static TsidFactory getTsidFactory() {
		return tsidFactory;
	}
}
