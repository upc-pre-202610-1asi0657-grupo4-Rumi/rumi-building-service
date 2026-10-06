package com.rumi.buildingmanagement.infrastructure.web;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfiguration {

    @Bean
    public OpenAPI buildingServiceOpenApi() {
        return new OpenAPI().info(new Info()
                .title("Rumi Building Service API")
                .description("REST API of the Building Management bounded context: "
                        + "buildings, their IoT sensors and resident invitations.")
                .version("0.1.0"));
    }
}
