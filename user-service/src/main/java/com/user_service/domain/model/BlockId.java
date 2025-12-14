package com.user_service.domain.model;

import lombok.*;

import java.io.Serializable;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@EqualsAndHashCode
public class BlockId implements Serializable {

	private Long blockerId;
	private Long blockedId;
}
