package com.coffeeshop.infrastructure.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {
    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(
                        new Info()
                                .title("Coffee Shop API")
                                .version("1.0.0")
                                .description(
                                        "Coffee Shop Management System API with Virtual Threads - "
                                                + "A production-like backend system built with Java 21, "
                                                + "demonstrating Virtual Threads, Clean Architecture, and modern best practices"))
                .addSecurityItem(new SecurityRequirement().addList("bearer-jwt"))
                .components(
                        new Components()
                                .addSecuritySchemes(
                                        "bearer-jwt",
                                        new SecurityScheme()
                                                .type(SecurityScheme.Type.HTTP)
                                                .scheme("bearer")
                                                .bearerFormat("JWT")
                                                .description("JWT token authentication")));
    }
}



