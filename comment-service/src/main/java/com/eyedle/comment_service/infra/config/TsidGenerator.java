package com.eyedle.comment_service.infra.config;

import java.io.Serializable;

import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.id.IdentifierGenerator;

public class TsidGenerator implements IdentifierGenerator {

	@Override
	public Serializable generate(SharedSessionContractImplementor session, Object object) {
		// Holder에 저장된 팩토리를 꺼내서 ID 생성 후 Long으로 반환
		return TsidHolder.getTsidFactory().create().toLong();
	}
}
