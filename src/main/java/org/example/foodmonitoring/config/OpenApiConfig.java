package org.example.foodmonitoring.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI foodMonitoringOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Food Monitoring API")
                        .version("1.0.0")
                        .description("REST API для мониторинга цен на продукты питания"));
    }
}
