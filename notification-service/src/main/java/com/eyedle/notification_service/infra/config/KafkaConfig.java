package com.eyedle.notification_service.infra.config;

import org.apache.kafka.common.TopicPartition;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.listener.CommonErrorHandler;
import org.springframework.kafka.listener.DeadLetterPublishingRecoverer;
import org.springframework.kafka.listener.DefaultErrorHandler;
import org.springframework.util.backoff.FixedBackOff;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Configuration
@EnableKafka
@RequiredArgsConstructor
public class KafkaConfig {

	private final KafkaTemplate<String, String> kafkaTemplate;

	@Bean
	public ConcurrentKafkaListenerContainerFactory<String, Object> kafkaListenerContainerFactory(
		ConsumerFactory<String, Object> consumerFactory
	) {
		ConcurrentKafkaListenerContainerFactory<String, Object> factory = new ConcurrentKafkaListenerContainerFactory<>();
		factory.setConsumerFactory(consumerFactory);

		factory.setCommonErrorHandler(errorHandler());

		return factory;
	}

	private CommonErrorHandler errorHandler(){
		DeadLetterPublishingRecoverer recoverer = new DeadLetterPublishingRecoverer(kafkaTemplate,
			(record, exception) -> {
				log.error("[kafka] 최종 실패 DLQ로 이동 : Topic: {}, Value: {}", record.topic(), record.value());
				// 이동할 타겟 토픽 지정
				return new TopicPartition(record.topic()+ ".DLT", record.partition());
			});

		// 재시도 전략 : 1초 간격으로 3번 시도 후 실패시 DLQ로
		return new DefaultErrorHandler(recoverer, new FixedBackOff(1000L,3));
	}

}
