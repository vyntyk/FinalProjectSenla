package org.example.foodmonitoring.config;

import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.tags.Tag;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@SecurityScheme(
        name = "JWT",
        type = SecuritySchemeType.HTTP,
        bearerFormat = "JWT",
        scheme = "bearer"
)
public class OpenApiConfig {

    @Bean
    public OpenAPI foodMonitoringOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Food Monitoring API")
                        .version("1.0.0")
                        .description("REST API для мониторинга цен на продукты питания"))
                .addSecurityItem(new SecurityRequirement().addList("JWT"))
                .addTagsItem(new Tag()
                        .name("Authentication")
                        .description("Endpoints for user registration and login"))
                .addTagsItem(new Tag()
                        .name("Web Pages")
                        .description("Endpoints for rendering web pages"))
                .addTagsItem(new Tag()
                        .name("Stores")
                        .description("Endpoints for managing stores"))
                .addTagsItem(new Tag()
                        .name("Users")
                        .description("Endpoints for managing users"));
    }
}