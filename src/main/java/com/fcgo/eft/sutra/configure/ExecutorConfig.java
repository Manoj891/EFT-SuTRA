package com.fcgo.eft.sutra.configure;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Configuration
public class ExecutorConfig {

    @Primary
    @Bean
    public ExecutorService executorService() {
        return Executors.newFixedThreadPool(500);
    }

    @Bean(name = "realTime")
    public ExecutorService realExecutorService() {
        return Executors.newFixedThreadPool(200);
    }

    @Bean(name = "nonRealTime")
    public ExecutorService nonRealTimeExecutorService() {
        return Executors.newFixedThreadPool(200);
    }


}
