package com.usabana.market.web.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.Contact;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Usabana Market API")
                        .description("API REST para el sistema de gestión de productos y compras del mercado Usabana")
                        .version("1.0.0")
                        .contact(new Contact()
                                .name("Equipo de Desarrollo 5")
                                .email("desarrollo@usabana.edu.co")
                        )
                );
    }
}