package com.eyedle.comment_service.presentation.dto;

import java.util.List;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class SliceResponse<T> {

	private final List<T> content;
	private final boolean hasNext;
	private final Long nextCursor;

	public static <T> SliceResponse<T> of(List<T> content, boolean hasNext, Long nextCursor){
		return new SliceResponse<>(content, hasNext, nextCursor);
	}
}
