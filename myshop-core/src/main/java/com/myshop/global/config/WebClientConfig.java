package com.myshop.global.config;

import io.netty.handler.logging.LogLevel;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.reactive.ClientHttpConnector;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import reactor.netty.http.client.HttpClient;
import reactor.netty.transport.logging.AdvancedByteBufFormat;
import java.time.Duration;

@Configuration
public class WebClientConfig {

    @Bean
    public WebClient webClient() {
        // Reactor Netty HttpClient를 사용하여 타임아웃 및 로깅 설정
        HttpClient httpClient = HttpClient.create()
                .responseTimeout(Duration.ofSeconds(10)) // 타임아웃 설정
                .wiretap("reactor.netty.http.client.HttpClient",
                        LogLevel.DEBUG,
                        AdvancedByteBufFormat.TEXTUAL); // 로깅 활성화

        ClientHttpConnector connector = new ReactorClientHttpConnector(httpClient);

        return WebClient.builder()
                .baseUrl("http://localhost:") // 기본 URL 설정
                .clientConnector(connector)
                .build();
    }
}
