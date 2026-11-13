package com.techdrawer.amy.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springdoc.core.customizers.OpenApiCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import java.util.Collections;

@Component
public class OpenApiConfig {

    @Bean
    public OpenAPI openAPI() {

        return new OpenAPI()
                .info(new Info()
                        .title("AMY Documentation")
                        .version("1.o")
                        .description("Application to connect artist, customer and visitor")
                )
                .addSecurityItem(new SecurityRequirement().addList("bearerAuth"))
                .components(new Components()
                        .addSecuritySchemes("bearerAuth",
                                new SecurityScheme()
                                        .name("bearerAuth")
                                        .type(SecurityScheme.Type.HTTP)
                                        .scheme("bearer")
                                        .bearerFormat("JWT")
                                        .description("Enter your token here.")
                                )
                );
    }

    @Bean
    public OpenApiCustomizer openApiCustomizer() {

        return openAPI -> {

            if (openAPI.getPaths() == null) return;

            openAPI.getPaths().forEach((path, item) -> {

                item.readOperationsMap().forEach((method, operation) -> {

                    Boolean isPublicGet = method.name().equalsIgnoreCase("GET") && (
                            path.equals("/api/artists/") ||
                                    path.equals("/api/articles/") ||
                                    path.equals("/api/articles/{publicId}") ||
                                    path.equals("/api/artists/speciality/{speciality}") ||
                                    path.equals("/api/artists/style/{style}") ||
                                    path.equals("/api/articles/artist/{publicId}") ||
                                    path.equals("/api/gallery/") ||
                                    path.equals("/api/gallery/{publicId}") ||
                                    path.equals("/api/gallery/{type}") ||
                                    path.equals("/api/gallery/{city}") ||
                                    path.equals("/api/exhibitions/") ||
                                    path.equals("/api/exhibitions/{publicId}") ||
                                    path.equals("/api/exhibitions/{city}")

                    );

                    Boolean isAuthRoute = path.startsWith("/api/auth");

                    Boolean refreshTokenRoute = path.startsWith("/api/refresh/");

                    if (isPublicGet || isAuthRoute || refreshTokenRoute) {
                        operation.setSecurity(Collections.emptyList());
                    }

                });

            });
        };
    }
}
