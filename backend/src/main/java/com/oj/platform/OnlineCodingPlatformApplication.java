package com.oj.platform;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

/**
 * Main entry point for the Online Coding Practice Platform Backend.
 *
 * Annotations used:
 * - @SpringBootApplication: Meta-annotation combining @Configuration, @EnableAutoConfiguration, and @ComponentScan.
 * - @EnableJpaAuditing: Automatically populates auditing fields (e.g. createdAt, updatedAt) on entities.
 */
@SpringBootApplication
@EnableJpaAuditing
public class OnlineCodingPlatformApplication {

    public static void main(String[] args) {
        SpringApplication.run(OnlineCodingPlatformApplication.class, args);
    }
}
