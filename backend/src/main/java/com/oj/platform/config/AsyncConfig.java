package com.oj.platform.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;

/**
 * Configuration for asynchronous task execution.
 *
 * Configures a dedicated ThreadPoolTaskExecutor for code evaluation tasks,
 * ensuring code execution does not block HTTP request threads or other async tasks.
 */
@Configuration
@EnableAsync
public class AsyncConfig {

    @Bean(name = "judgeExecutorPool")
    public Executor judgeExecutorPool() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(4);
        executor.setMaxPoolSize(8);
        executor.setQueueCapacity(100);
        executor.setThreadNamePrefix("JudgeWorker-");
        executor.initialize();
        return executor;
    }
}
