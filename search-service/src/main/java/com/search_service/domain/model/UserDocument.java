package com.search_service.domain.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Document(indexName = "users")
public class UserDocument {

	@Id
	private Long id;

	@Field(type = FieldType.Text, analyzer = "nori")
	private String username;

	@Field(type = FieldType.Keyword, index = false)
	private String profileImageUrl;

	@Field(type = FieldType.Keyword, index = false) // 상태값(ACTIVE 등) 필터링용
	private String status;
}
