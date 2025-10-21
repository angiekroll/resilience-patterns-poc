/**
 * Copyright 2025, Company. All rights reserved Date: 20/10/25
 */
package com.resiliencepatterns.poc.infrastructure.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.info.License;
import io.swagger.v3.oas.annotations.servers.Server;
import org.springframework.context.annotation.Configuration;

/**
 *
 * @author angiekroll@gmail.com - Ángela Carolina Castillo Rodríguez.
 * @version - 1.0.0
 * @since - 1.0.0
 */

@Configuration
@OpenAPIDefinition(
    info = @Info(
        title = "Resilience Patterns POC API - Appgate",
        description = """
            API demonstrating resilience patterns in microservices.
            
            This API showcases production-ready patterns for:
            - Circuit Breaker protection
            - Rate limiting strategies  
            - Retry mechanisms with exponential backoff
            - Graceful degradation
            
            Built with Hexagonal Architecture principles for maintainability and testability.
            """,
        version = "1.0.0",
        contact = @Contact(
            name = "Ángela Carolina Castillo Rodríguez",
            email = "angiekroll@gmail.com"
        ),
        license = @License(
            name = "MIT License",
            url = "https://opensource.org/licenses/MIT"
        )
    ),
    servers = {
        @Server(url = "http://localhost:8080", description = "Development server"),
        @Server(url = "https://qa.resiliencepatterns.appgate.com", description = "QA server"),
        @Server(url = "https://api.resiliencepatterns.appgate.com", description = "Production server")
    }
)
public class OpenApiConfig {

}