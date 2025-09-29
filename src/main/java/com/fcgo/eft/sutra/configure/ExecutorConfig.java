package com.fcgo.eft.sutra.configure;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;


import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

@Configuration
public class ExecutorConfig {
    @Primary
    @Bean
    public ThreadPoolExecutor executorService() {
        return (ThreadPoolExecutor) Executors.newFixedThreadPool(100);
    }

    @Bean(name = "realTime")
    public ThreadPoolExecutor realExecutorService() {
        return (ThreadPoolExecutor) Executors.newFixedThreadPool(100);
    }

    @Bean(name = "nonRealTime")
    public ThreadPoolExecutor nonRealTimeExecutorService() {
        return (ThreadPoolExecutor) Executors.newFixedThreadPool(100);
    }


}
