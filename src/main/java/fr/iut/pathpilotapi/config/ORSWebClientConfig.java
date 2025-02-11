package fr.iut.pathpilotapi.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class ORSWebClientConfig {
    @Value("${openrouteservice.api-key}")
    private static final String API_KEY = "Update the API key in the environment variables";
    private static final String API_BASE_URL = "https://api.openrouteservice.org/v2";

    @Bean
    public WebClient oRSWebClient(WebClient.Builder builder) {
        return builder
                .baseUrl(API_BASE_URL)
                .defaultHeader("Authorization", "Bearer " + API_KEY)
                .defaultHeader("Content-Type", "application/json").build();
    }
}