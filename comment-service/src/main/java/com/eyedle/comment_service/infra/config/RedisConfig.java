package com.eyedle.comment_service.infra.config;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@Configuration
public class RedisConfig {

	@Value("${spring.data.redis.host}")
	private String cacheHost;

	@Value("${spring.data.redis.port}")
	private int cachePort;

	@Value("${global.redis.host}")
	private String globalHost;

	@Value("${global.redis.port}")
	private int globalPort;

	@Bean
	@Primary
	public RedisConnectionFactory cacheConnectionFactory() {
		return new LettuceConnectionFactory(cacheHost, cachePort);
	}

	@Bean
	@Primary
	public RedisTemplate<String, Object> redisTemplate(
		@Qualifier("cacheConnectionFactory") RedisConnectionFactory connectionFactory) {

		RedisTemplate<String, Object> template = new RedisTemplate<>();
		template.setConnectionFactory(connectionFactory);

		// 직렬화 설정 (Key는 String, Value는 JSON)
		template.setKeySerializer(new StringRedisSerializer());
		template.setValueSerializer(new GenericJackson2JsonRedisSerializer());

		// Hash 자료구조를 쓸 경우를 대비한 설정
		template.setHashKeySerializer(new StringRedisSerializer());
		template.setHashValueSerializer(new GenericJackson2JsonRedisSerializer());

		return template;
	}

	@Bean
	public RedisConnectionFactory globalConnectionFactory() {
		return new LettuceConnectionFactory(globalHost, globalPort);
	}

	@Bean(name = "globalRedisTemplate") // 이름 지정 필수
	public RedisTemplate<String, Object> globalRedisTemplate(
		@Qualifier("globalConnectionFactory") RedisConnectionFactory connectionFactory) {

		RedisTemplate<String, Object> template = new RedisTemplate<>();
		template.setConnectionFactory(connectionFactory);

		// Pub/Sub 메시지도 JSON으로 주고받기 위해 동일하게 설정
		template.setKeySerializer(new StringRedisSerializer());
		template.setValueSerializer(new GenericJackson2JsonRedisSerializer());

		return template;
	}

}
