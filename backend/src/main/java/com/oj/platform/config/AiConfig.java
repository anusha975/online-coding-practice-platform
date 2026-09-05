package com.oj.platform.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestClient;

import java.time.Duration;

/**
 * Configuration for AI service HTTP clients and timeout policies.
 */
@Configuration
public class AiConfig {

    @Value("${app.ai.timeout-ms:30000}")
    private int timeoutMs;

    @Bean
    public RestClient aiRestClient() {
        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        factory.setConnectTimeout((int) Duration.ofSeconds(5).toMillis());
        factory.setReadTimeout(timeoutMs);

        return RestClient.builder()
                .requestFactory(factory)
                .build();
    }
}
