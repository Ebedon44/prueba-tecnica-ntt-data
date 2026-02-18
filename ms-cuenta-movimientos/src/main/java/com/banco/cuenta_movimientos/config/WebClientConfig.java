package com.banco.cuenta_movimientos.config;


import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.beans.factory.annotation.Value;

@Configuration
public class WebClientConfig {

    @Value("${api.client.ms-persona-url}")
    private String msPersonaUrl;

    @Bean
    public WebClient webClient() {
        return WebClient.builder()
                .baseUrl(msPersonaUrl)
                .build();
    }
}
