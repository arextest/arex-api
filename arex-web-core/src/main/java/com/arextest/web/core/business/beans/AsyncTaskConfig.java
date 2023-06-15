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

    // @Bean("compare-task-executor")
    // public ThreadPoolTaskExecutor compareTaskExecutor() {
    //     ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
    //     executor.setCorePoolSize(2);
    //     executor.setMaxPoolSize(8);
    //     executor.setKeepAliveSeconds(30);
    //     executor.setQueueCapacity(1000);
    //     executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
    //     executor.setWaitForTasksToCompleteOnShutdown(true);
    //     executor.setAllowCoreThreadTimeOut(true);
    //     executor.setThreadNamePrefix("compare-task-executor-");
    //     return executor;
    // }

    @Bean("recovery-items-executor")
    public ThreadPoolTaskExecutor recoveryItemsExecutor() {
        return newExecutor("recovery-items-executor-", 2, 8, 60, 1000);
    }

    @Bean("sending-mail-executor")
    public ThreadPoolTaskExecutor sendingMailExecutor() {
        return newExecutor("sending-mail-executor-", 1, 4, 60, 1000);
    }

    @Bean("message-clip-executor")
    public ThreadPoolTaskExecutor messageClipTaskExecutor() {
        return newExecutor("message-clip-executor-", 2, 4, 30, 1000);
    }

    @Bean("report-scene-executor")
    public ThreadPoolTaskExecutor reportSceneExecutor() {
        return newExecutor("report-scene-executor-", 2, 4, 60, 1000);
    }

    @Bean("report-statistic-executor")
    public ThreadPoolTaskExecutor reportStatisticExecutor() {
        return newExecutor("report-statistic-executor-", 2, 4, 60, 1000);
    }

    private ThreadPoolTaskExecutor newExecutor(String namePrefix,
            int corePoolSize,
            int maxPoolSize,
            int keepAliveSeconds,
            int queueCapacity) {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(corePoolSize);
        executor.setMaxPoolSize(maxPoolSize);
        executor.setKeepAliveSeconds(keepAliveSeconds);
        executor.setQueueCapacity(queueCapacity);
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        executor.setWaitForTasksToCompleteOnShutdown(true);
        executor.setAllowCoreThreadTimeOut(true);
        executor.setThreadNamePrefix(namePrefix);
        return executor;
    }
}
