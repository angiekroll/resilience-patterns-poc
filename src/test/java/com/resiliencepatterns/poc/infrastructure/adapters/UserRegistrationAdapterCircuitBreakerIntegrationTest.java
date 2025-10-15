/**
 * Copyright 2025, Company. All rights reserved Date: 9/10/25
 */
package com.resiliencepatterns.poc.infrastructure.adapters;

import static org.awaitility.Awaitility.await;
import static org.junit.jupiter.api.Assertions.assertEquals;

import com.resiliencepatterns.poc.application.dto.RegisterUserCommand;
import com.resiliencepatterns.poc.domain.port.in.RegisterUserUseCasePort;
import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;
import java.util.concurrent.TimeUnit;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.wait.strategy.Wait;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

/**
 *
 * @author angiekroll@gmail.com - Ángela Carolina Castillo Rodríguez.
 * @version - 1.0.0
 * @since - 1.0.0
 */

@SpringBootTest
@Testcontainers
@ActiveProfiles("test")
public class UserRegistrationAdapterCircuitBreakerIntegrationTest {

  @Container
  static GenericContainer<?> wireMockContainer = new GenericContainer<>(DockerImageName.parse("wiremock/wiremock:3.3.1"))
      .withExposedPorts(8080)
      .withFileSystemBind("testing/wiremock/mappings", "/home/wiremock/mappings")
      .withFileSystemBind("testing/wiremock/__files", "/home/wiremock/__files")
      .waitingFor(Wait.forHttp("/__admin").forStatusCode(200));

  @Autowired
  private RegisterUserUseCasePort registerUserUseCasePort;

  @Autowired
  private CircuitBreakerRegistry circuitBreakerRegistry;

  @DynamicPropertySource
  static void registerProperties(DynamicPropertyRegistry registry) {
    registry.add("external-api.base-url", () ->
        "http://localhost:" + wireMockContainer.getMappedPort(8080));
  }

  @Test
  void shouldOpenCircuitAfterFailures() {
    // Obtener instancia del circuit breaker
    CircuitBreaker circuitBreaker = circuitBreakerRegistry.circuitBreaker("external-api");

    // Generar suficientes fallos para abrir el circuito
    for (int i = 0; i < 5; i++) {
      try {
        registerUserUseCasePort.registerUser(
            RegisterUserCommand.create(1L, "Test User", "test" + i + "@example.com")
        );
      } catch (Exception ignored) {
        // Esperamos excepciones durante la simulación de fallos
      }
    }

    // Verificar que el circuito se abre
    await()
        .atMost(5, TimeUnit.SECONDS)
        .until(() -> circuitBreaker.getState() == CircuitBreaker.State.OPEN);

    assertEquals(CircuitBreaker.State.OPEN, circuitBreaker.getState());
  }

}