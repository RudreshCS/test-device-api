package com.example.deviceapi.config;

import io.swagger.v3.oas.models.ExternalDocumentation;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.OpenAPI;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI deviceOpenAPI() {
        return new OpenAPI()
                .info(new Info().title("Device Data API")
                        .description("API to store and manage device telemetry data")
                        .version("1.0.0")
                        .contact(new Contact().name("Dev Team").email("dev@example.com")))
                .externalDocs(new ExternalDocumentation().description("Project Repo").url("https://example.com"));
    }
}
