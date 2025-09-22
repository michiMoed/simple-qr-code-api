package com.micmoe.simple_qr.Configs;

import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.OpenAPI;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfiguration {
    @Bean
    public OpenAPI customOpenApi() {
        return new OpenAPI().info(new Info().title("Simple QR API").version("1.0").description("Simple QR Code API that allows users to generate and decode custom QR Codes."));
    }
}
