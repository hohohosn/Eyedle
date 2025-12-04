package com.search_service.domain.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class KeywordScore {
	private String keyword;
	private double score;
}
