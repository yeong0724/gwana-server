package com.gwana.server.common.config;

import com.gwana.server.common.interceptor.AuditingInterceptor;
import com.gwana.server.common.security.JwtTokenProvider;
import org.mybatis.spring.boot.autoconfigure.ConfigurationCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MyBatisConfig {
    @Bean
    public AuditingInterceptor auditingInterceptor(JwtTokenProvider jwtTokenProvider) {
        return new AuditingInterceptor(jwtTokenProvider);
    }

    @Bean
    public ConfigurationCustomizer mybatisConfigurationCustomizer(AuditingInterceptor auditingInterceptor) {
        return configuration -> {
            configuration.addInterceptor(auditingInterceptor);
        };
    }
}