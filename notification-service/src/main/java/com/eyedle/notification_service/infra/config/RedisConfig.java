package com.eyedle.notification_service.infra.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.listener.PatternTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.data.redis.listener.adapter.MessageListenerAdapter;

import com.eyedle.notification_service.infra.redis.RedisSubscriber;

@Configuration
public class RedisConfig {

	public static final String SSE_TOPIC = "sse:notification";

	@Bean
	public RedisMessageListenerContainer redisMessageListenerContainer(
		RedisConnectionFactory connectionFactory,
		MessageListenerAdapter listenerAdapter
	){
		RedisMessageListenerContainer container = new RedisMessageListenerContainer();
		container.setConnectionFactory(connectionFactory);
		container.addMessageListener(listenerAdapter, new PatternTopic(SSE_TOPIC));
		return container;
	}

	@Bean
	public MessageListenerAdapter listenerAdapter(RedisSubscriber subscriber) {
		return new MessageListenerAdapter(subscriber, "sendMessage");
	}

}
