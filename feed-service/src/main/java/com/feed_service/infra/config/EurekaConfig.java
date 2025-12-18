package com.feed_service.infra.config;

import org.springframework.cloud.commons.util.InetUtils;
import org.springframework.cloud.netflix.eureka.EurekaInstanceConfigBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class EurekaConfig {

    @Bean
    public EurekaInstanceConfigBean eurekaInstanceConfig(InetUtils inetUtils) {
        EurekaInstanceConfigBean config = new EurekaInstanceConfigBean(inetUtils);

        // 1. InetUtils를 통해 169.254가 아닌 실제 IP 찾기 시도
        String ip = inetUtils.findFirstNonLoopbackAddress().getHostAddress();

        // 2. 만약 여전히 169로 시작한다면, Fargate 환경 정보를 직접 참조하도록 설정
        config.setIpAddress(ip);
        config.setPreferIpAddress(true);
        config.setInstanceId(String.format("%s:%s:%d", "feed-service", ip, 19300));

        return config;
    }
}
