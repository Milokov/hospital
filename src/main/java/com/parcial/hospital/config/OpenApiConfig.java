package com.parcial.hospital.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {
    @Bean
    public OpenAPI hospitalOpenApi() {
        return new OpenAPI()
                .info(new Info()
                        .title("Hospital API")
                        .version("1.0.0")
                        .description("Gestion de consultas medicas con roles ADMINISTRADOR, MEDICO y PACIENTE"));
    }
}
