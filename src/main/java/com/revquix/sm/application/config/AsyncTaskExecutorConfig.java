package com.revquix.sm.application.config;

/*
  Developer: Rohit Parihar
  Project: revquix-sm
  GitHub: github.com/rohit-zip
  File: AsyncTaskExecutorConfig
 */

import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import jakarta.annotation.PreDestroy;
import java.util.concurrent.Executor;
import java.util.concurrent.ThreadPoolExecutor;

@Configuration
@EnableAsync
@Slf4j
public class AsyncTaskExecutorConfig {

    @Value("${async.executor.core-pool-size:5}")
    private int corePoolSize;

    @Value("${async.executor.max-pool-size:10}")
    private int maxPoolSize;

    @Value("${async.executor.queue-capacity:100}")
    private int queueCapacity;

    @Value("${async.executor.keep-alive-seconds:60}")
    private int keepAliveSeconds;

    @Value("${async.executor.await-termination-seconds:20}")
    private int awaitTerminationSeconds;

    private ThreadPoolTaskExecutor taskExecutor;

    @Bean(name = "eventTaskExecutor")
    public Executor eventTaskExecutor() {
        log.info("Creating EventTaskExecutor with corePoolSize: {}, maxPoolSize: {}, queueCapacity: {}",
                   corePoolSize, maxPoolSize, queueCapacity);

        taskExecutor = new ThreadPoolTaskExecutor();
        taskExecutor.setCorePoolSize(corePoolSize);
        taskExecutor.setMaxPoolSize(maxPoolSize);
        taskExecutor.setQueueCapacity(queueCapacity);
        taskExecutor.setKeepAliveSeconds(keepAliveSeconds);
        taskExecutor.setThreadNamePrefix("event-async-");

        // Use CallerRunsPolicy for production - prevents task rejection and provides backpressure
        taskExecutor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());

        // Enable graceful shutdown
        taskExecutor.setWaitForTasksToCompleteOnShutdown(true);
        taskExecutor.setAwaitTerminationSeconds(awaitTerminationSeconds);

        // Allow core threads to timeout for better resource management
        taskExecutor.setAllowCoreThreadTimeOut(true);

        taskExecutor.initialize();

        log.info("EventTaskExecutor initialized successfully");
        return taskExecutor;
    }

    @PreDestroy
    public void destroy() {
        if (taskExecutor != null) {
            log.info("Shutting down EventTaskExecutor gracefully");
            taskExecutor.shutdown();
        }
    }
}
