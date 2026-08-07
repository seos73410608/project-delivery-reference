package com.seos.pmis.config.swagger;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    private static final String SECURITY_SCHEME_NAME = "Bearer Authentication";

    @Bean
    public OpenAPI openAPI() {

        Info info = new Info()
                .title("PMIS REST API")
                .description("""
                        Project Management Information System API

                        • JWT Authentication
                        • Project Management
                        • WBS
                        • Schedule
                        • Issue
                        • Risk
                        • Change
                        • CMDB
                        • Spring AI
                        """)
                .version("v0.2.0")
                .contact(
                        new Contact()
                                .name("Seo Seokhyeon")
                                .email("seos73410608@gmail.com")
                )
                .license(
                        new License()
                                .name("MIT License")
                );

        SecurityScheme securityScheme =
                new SecurityScheme()
                        .name("Authorization")
                        .type(SecurityScheme.Type.HTTP)
                        .scheme("bearer")
                        .bearerFormat("JWT")
                        .in(SecurityScheme.In.HEADER);

        SecurityRequirement securityRequirement =
                new SecurityRequirement()
                        .addList(SECURITY_SCHEME_NAME);

        return new OpenAPI()
                .info(info)
                .components(
                        new Components()
                                .addSecuritySchemes(
                                        SECURITY_SCHEME_NAME,
                                        securityScheme
                                )
                )
                .addSecurityItem(securityRequirement);
    }
}