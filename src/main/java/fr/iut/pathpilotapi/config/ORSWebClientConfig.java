package fr.iut.pathpilotapi.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class ORSWebClientConfig {
    private static final String API_BASE_URL = "https://api.openrouteservice.org/v2";
    private static final String API_KEY = "5b3ce3597851110001cf6248a7c14d937e0a4c0d850c723cff110a2b";

    @Bean
    public WebClient oRSWebClient(WebClient.Builder builder) {
        return builder
                .baseUrl(API_BASE_URL)
                .defaultHeader("Authorization", "Bearer " + API_KEY)
                .defaultHeader("Content-Type", "application/json").build();
    }
}