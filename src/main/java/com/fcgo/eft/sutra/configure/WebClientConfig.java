package com.fcgo.eft.sutra.configure;

import io.netty.channel.ChannelOption;
import io.netty.handler.ssl.SslContextBuilder;
import io.netty.handler.ssl.util.InsecureTrustManagerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.web.reactive.function.client.ExchangeStrategies;
import reactor.netty.http.client.HttpClient;
import reactor.netty.tcp.SslProvider;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.reactive.function.client.WebClient;

import javax.net.ssl.SSLException;
import java.time.Duration;

@Configuration
public class WebClientConfig {

    @Primary
    @Bean(name = "WebClient")
    public WebClient webClient() throws SSLException {
        // ✅ Correct: build the SSLContext separately
        SslProvider sslProvider = SslProvider.builder()
                .sslContext(SslContextBuilder.forClient()
                        .trustManager(InsecureTrustManagerFactory.INSTANCE) // trust-all (testing only)
                        .build())  // build SslContext here
                .handshakeTimeout(Duration.ofSeconds(60)) // handshake timeout
                .build();

        HttpClient httpClient = HttpClient.create()
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 1000 * 60) // TCP connect timeout
                .secure(sslProvider)                                // custom SSL provider
                .responseTimeout(Duration.ofSeconds(60));           // response timeout

        return WebClient.builder()
                .clientConnector(new ReactorClientHttpConnector(httpClient))
                .exchangeStrategies(ExchangeStrategies.builder()
                        .codecs(configurer -> configurer.defaultCodecs()
                                .maxInMemorySize(100 * 1024 * 1024)) // 512 MB
                        .build())
                .build();
    }

    @Bean(name = "fetchBankAccountDetails")
    public WebClient fetchBankAccountDetails() throws SSLException {
        // ✅ Correct: build the SSLContext separately
        SslProvider sslProvider = SslProvider.builder()
                .sslContext(SslContextBuilder.forClient()
                        .trustManager(InsecureTrustManagerFactory.INSTANCE) // trust-all (testing only)
                        .build())  // build SslContext here
                .handshakeTimeout(Duration.ofSeconds(1800)) // handshake timeout
                .build();

        HttpClient httpClient = HttpClient.create()
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 1000 * 60 * 30) // TCP connect timeout
                .secure(sslProvider)                                // custom SSL provider
                .responseTimeout(Duration.ofSeconds(1800));           // response timeout

        return WebClient.builder()
                .clientConnector(new ReactorClientHttpConnector(httpClient))
                .exchangeStrategies(ExchangeStrategies.builder()
                        .codecs(configurer -> configurer.defaultCodecs()
                                .maxInMemorySize(1024 * 1024 * 1024)) // 512 MB
                        .build())
                .build();
    }
}
