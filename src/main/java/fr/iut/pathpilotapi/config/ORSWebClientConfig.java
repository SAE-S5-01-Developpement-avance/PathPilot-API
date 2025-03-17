package fr.iut.pathpilotapi.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class ORSWebClientConfig {
    /**
     * API_KEY is the key to access the OpenRouteService API
     * <p>
     *     The key is stored in the application.properties file
     * </p>
     */
    @Value("${openrouteservice.api-key}")
    private String API_KEY;
    private static final String API_BASE_URL = "https://api.openrouteservice.org/v2";

    @Bean
    public WebClient oRSWebClient(WebClient.Builder builder) {
        return builder
                .baseUrl(API_BASE_URL)
                .defaultHeader("Authorization", "Bearer " + API_KEY)
                .defaultHeader("Content-Type", "application/json").build();
    }
}