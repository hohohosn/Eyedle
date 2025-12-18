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

        // Fargate 컨테이너의 실제 Private IP를 찾아 할당
        String ip = inetUtils.findFirstNonLoopbackAddress().getHostAddress();

        config.setIpAddress(ip);
        config.setPreferIpAddress(true);
        // 서비스 이름과 IP, 포트로 ID 생성
        config.setInstanceId(String.format("%s:%s:%d", "feed-service", ip, 19300));

        return config;
    }
}
