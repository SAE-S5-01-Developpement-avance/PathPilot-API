/*
 * SwaggerConfig.java                                  26 nov. 2024
 * IUT de Rodez, no author rights
 */

package fr.iut.pathpilotapi.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("PathPilot API")
                        .version("1.0")
                        .description("API documentation for PathPilot"));
    }
}
