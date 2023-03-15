package com.arextest.web.core.business.beans;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.ThreadPoolExecutor;

/**
 * Created by rchen9 on 2022/7/12.
 */
@Configuration
public class AsyncTaskConfig {

    @Bean("compare-task-executor")
    public ThreadPoolTaskExecutor compareTaskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(2);
        executor.setMaxPoolSize(8);
        executor.setKeepAliveSeconds(30);
        executor.setQueueCapacity(1000);
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        executor.setWaitForTasksToCompleteOnShutdown(true);
        executor.setAllowCoreThreadTimeOut(true);
        executor.setThreadNamePrefix("compare-task-executor-");
        return executor;
    }

    @Bean("recovery-items-executor")
    public ThreadPoolTaskExecutor recoveryItemsExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(2);
        executor.setMaxPoolSize(8);
        executor.setKeepAliveSeconds(60);
        executor.setQueueCapacity(1000);
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        executor.setWaitForTasksToCompleteOnShutdown(true);
        executor.setAllowCoreThreadTimeOut(true);
        executor.setThreadNamePrefix("recovery-items-executor-");
        return executor;
    }

    @Bean("sending-mail-executor")
    public ThreadPoolTaskExecutor sendingMailExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(1);
        executor.setMaxPoolSize(4);
        executor.setKeepAliveSeconds(60);
        executor.setQueueCapacity(1000);
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        executor.setWaitForTasksToCompleteOnShutdown(true);
        executor.setAllowCoreThreadTimeOut(true);
        executor.setThreadNamePrefix("sending-mail-executor-");
        return executor;
    }

    @Bean("message-clip-executor")
    public ThreadPoolTaskExecutor messageClipTaskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(2);
        executor.setMaxPoolSize(4);
        executor.setKeepAliveSeconds(30);
        executor.setQueueCapacity(1000);
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        executor.setWaitForTasksToCompleteOnShutdown(true);
        executor.setAllowCoreThreadTimeOut(true);
        executor.setThreadNamePrefix("message-clip-executor-");
        return executor;
    }
}
