package com.example.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

import lombok.Getter;
import lombok.Setter;

@Configuration
@ConfigurationProperties(prefix = "confluence")
@Setter
@Getter
public class ConfluenceWebClientConfig {

    private String username;
    private String apiToken;
    private String baseUrl;

    @Bean
    public WebClient confluenceWebClient() {
        String authToken = "Basic " + java.util.Base64.getEncoder()
        .encodeToString((this.username + ":" + this.apiToken).getBytes());
        return WebClient.builder()
                .baseUrl(this.baseUrl)
                .defaultHeader("Authorization", authToken)
                .defaultHeader("Content-Type", "application/json")
                .defaultHeader("Accept", "application/json")
                .build();
    }
}
