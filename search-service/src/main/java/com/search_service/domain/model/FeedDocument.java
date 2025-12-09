package com.search_service.domain.model;

import java.time.LocalDateTime;
import java.util.List;

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
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Document(indexName = "feeds")
public class FeedDocument {

	@Id
	private Long id;

	@Field(type = FieldType.Text, analyzer = "nori")
	private String content;

	@Field(type = FieldType.Keyword)
	private List<String> tags;

	@Field(type = FieldType.Keyword, index = false)
	private String imageUrl;

	@Field(type = FieldType.Keyword, index = false)
	private Long userId;

	@Field(type = FieldType.Keyword, index = false)
	private String username;

	@Field(type = FieldType.Keyword, index = false)
	private String userProfileUrl;

	@Field(type = FieldType.Integer)
	private Integer likeCount;

	@Field(type = FieldType.Date)
	private LocalDateTime createdAt;

	@Field(type = FieldType.Boolean)
	private Boolean isDeleted;
}
