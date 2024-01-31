package com.myshop.global.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springdoc.core.GroupedOpenApi;
import org.springdoc.core.customizers.OpenApiCustomiser;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@OpenAPIDefinition(servers = {@io.swagger.v3.oas.annotations.servers.Server(url = "/", description = "API Server")})
public class SwaggerConfig {
    @Bean
    public GroupedOpenApi customTestOpenAPi() {
        return GroupedOpenApi
                .builder()
                .group("APIS")
                .addOpenApiCustomiser(buildSecurityOpenApi())
                .build();
    }

    public OpenApiCustomiser buildSecurityOpenApi() {
        return openApi -> openApi.addSecurityItem(new SecurityRequirement().addList("JWTAuth"))
                .getComponents()
                .addSecuritySchemes("JWTAuth", new SecurityScheme()
                        .name("Authorization") // 'Authorization' 헤더 이름
                        .type(SecurityScheme.Type.HTTP)
                        .scheme("bearer")
                        .bearerFormat("JWT")); // JWT 토큰 형식 지정
    }
}
