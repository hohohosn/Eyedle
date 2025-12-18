package com.feed_service.infra.config;

import feign.RequestInterceptor;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@Configuration
public class FeignConfig {

    @Bean
    public RequestInterceptor requestInterceptor() {
        return requestTemplate -> {
            ServletRequestAttributes attrs =
                    (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();

            if (attrs != null) {
                HttpServletRequest req = attrs.getRequest();
                String userId = req.getHeader("X-User-Id");
                String userRole = req.getHeader("X-User-Role");

                if (userId != null) {
                    requestTemplate.header("X-User-Id", userId);
                    if (userRole != null) {
                        requestTemplate.header("X-User-Role", userRole);
                    }
                }
            }
        };
    }
}
