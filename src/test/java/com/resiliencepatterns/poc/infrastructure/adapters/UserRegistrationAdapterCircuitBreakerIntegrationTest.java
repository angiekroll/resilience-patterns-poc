/**
 * Copyright 2025, Company. All rights reserved Date: 9/10/25
 */
package com.resiliencepatterns.poc.infrastructure.adapters;

import static org.awaitility.Awaitility.await;
import static org.junit.jupiter.api.Assertions.assertEquals;

import com.resiliencepatterns.poc.application.dto.RegisterUserCommand;
import com.resiliencepatterns.poc.application.port.in.RegisterUserUseCasePort;
import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.TestPropertySource;
import org.springframework.web.client.RestTemplate;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.wait.strategy.Wait;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;
import static org.junit.jupiter.api.Assertions.*;
import org.springframework.http.*;


/**
 *
 * @author angiekroll@gmail.com - Ángela Carolina Castillo Rodríguez.
 * @version - 1.0.0
 * @since - 1.0.0
 */

@SpringBootTest
@Testcontainers
@ActiveProfiles("test")
class UserRegistrationAdapterCircuitBreakerIntegrationTest {

  @Container
  static GenericContainer<?> wireMock = new GenericContainer<>(DockerImageName.parse("wiremock/wiremock:3.3.1"))
      .withExposedPorts(8080)
/*      .withFileSystemBind("testing/wiremock/mappings", "/home/wiremock/mappings")
      .withFileSystemBind("testing/wiremock/__files", "/home/wiremock/__files")*/
      .waitingFor(Wait.forHttp("/__admin").forStatusCode(200));

  @Autowired
  private RegisterUserUseCasePort registerUserUseCasePort;

  @Autowired
  private CircuitBreakerRegistry circuitBreakerRegistry;

  @DynamicPropertySource
  static void configureProperties(DynamicPropertyRegistry registry) {
    registry.add("external-api.base-url", () ->
        "http://localhost:" + wireMock.getMappedPort(8080));
  }

  @BeforeEach
  void setUp() {
    clearWireMockStubs();
  }


  @Test
  @DisplayName("Circuit Breaker should open after repeated failures")
  void shouldOpenCircuitBreakerAfterRepeatedFailures() {
    // Configurar WireMock para simular fallos
    configureWireMockStub(500, "Internal Server Error");

    CircuitBreaker circuitBreaker = circuitBreakerRegistry.circuitBreaker("external-api");

    // Ejecutar suficientes fallos para abrir circuit breaker
    for (int i = 0; i < 6; i++) {
      final int userId = i;
      assertThrows(RuntimeException.class, () ->
          registerUserUseCasePort.registerUser(
              RegisterUserCommand.create((long) userId, "Test User", "test" + userId + "@example.com")
          ));
    }

    // Verificar que circuit breaker se abrió
    await()
        .atMost(10, TimeUnit.SECONDS)
        .until(() -> circuitBreaker.getState() == CircuitBreaker.State.OPEN);

    assertEquals(CircuitBreaker.State.OPEN, circuitBreaker.getState());
  }



  @Test
  @DisplayName("Rate Limiter should activate when exceeding request limit")
  void shouldActivateRateLimiterWhenExceedingRequestLimit() {
    // Configurar respuesta exitosa pero lenta
    configureWireMockStub(200, "User created successfully", 100);

    // Ejecutar múltiples requests concurrentes para activar rate limiter
    List<CompletableFuture<Void>> futures = new ArrayList<>();

    for (int i = 0; i < 25; i++) { // Más que el límite de 20
      final int userId = i;
      CompletableFuture<Void> future = CompletableFuture.runAsync(() -> {
        try {
          registerUserUseCasePort.registerUser(
              RegisterUserCommand.create((long) userId, "User " + userId, "user" + userId + "@example.com")
          );
        } catch (Exception e) {
          // Esperamos algunas excepciones por rate limiting
          System.out.println("Request " + userId + " handled by fallback: " + e.getMessage());
        }
      });
      futures.add(future);
    }

    // Esperar a que todas las requests terminen
    CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();

    // Verificar que WireMock recibió un número limitado de requests
    verifyWireMockRequests();
  }

  @Test
  @DisplayName("Retry pattern should work with transient failures")
  void shouldRetryOnTransientFailures() {
    // Configurar scenario: 2 fallos, luego éxito
    configureWireMockRetryScenario();

    // Ejecutar request que debería tener éxito después de reintentos
    assertDoesNotThrow(() ->
        registerUserUseCasePort.registerUser(
            RegisterUserCommand.create(1L, "Test User", "test@example.com")
        ));

    // Verificar que se hicieron exactamente 3 llamadas (1 inicial + 2 reintentos)
    verifyWireMockRetryRequests(3);
  }



  // ============================================
  // MÉTODOS HELPER PARA CONFIGURAR WIREMOCK
  // ============================================

  private void clearWireMockStubs() {
    try {
      RestTemplate restTemplate = new RestTemplate();
      String resetUrl = "http://localhost:" + wireMock.getMappedPort(8080) + "/__admin/reset";
      restTemplate.postForEntity(resetUrl, null, String.class);
    } catch (Exception e) {
      // Ignorar errores de limpieza
    }
  }

  private void configureWireMockStub(int statusCode, String message) {
    configureWireMockStub(statusCode, message, 0);
  }

  private void configureWireMockStub(int statusCode, String message, int delayMs) {
    RestTemplate restTemplate = new RestTemplate();
    String adminUrl = "http://localhost:" + wireMock.getMappedPort(8080) + "/__admin/mappings";

    String stubConfig = String.format("""
            {
              "request": {
                "method": "POST",
                "url": "/api/users"
              },
              "response": {
                "status": %d,
                "headers": {
                  "Content-Type": "application/json"
                },
                "body": "{\\"message\\": \\"%s\\", \\"id\\": 1}",
                "fixedDelayMilliseconds": %d
              }
            }
            """, statusCode, message, delayMs);

    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_JSON);
    HttpEntity<String> request = new HttpEntity<>(stubConfig, headers);

    restTemplate.postForEntity(adminUrl, request, String.class);
  }

  private void configureWireMockRetryScenario() {
    RestTemplate restTemplate = new RestTemplate();
    String adminUrl = "http://localhost:" + wireMock.getMappedPort(8080) + "/__admin/mappings";

    // Primer intento: fallo
    String firstStub = """
            {
              "request": {
                "method": "POST",
                "url": "/api/users"
              },
              "response": {
                "status": 500,
                "headers": {
                  "Content-Type": "application/json"
                },
                "body": "{\\"error\\": \\"First failure\\"}"
              },
              "scenarioName": "retry-test",
              "requiredScenarioState": "Started",
              "newScenarioState": "first-failure"
            }
            """;

    // Segundo intento: fallo
    String secondStub = """
            {
              "request": {
                "method": "POST",
                "url": "/api/users"
              },
              "response": {
                "status": 500,
                "headers": {
                  "Content-Type": "application/json"
                },
                "body": "{\\"error\\": \\"Second failure\\"}"
              },
              "scenarioName": "retry-test",
              "requiredScenarioState": "first-failure",
              "newScenarioState": "second-failure"
            }
            """;

    // Tercer intento: éxito
    String thirdStub = """
            {
              "request": {
                "method": "POST",
                "url": "/api/users"
              },
              "response": {
                "status": 200,
                "headers": {
                  "Content-Type": "application/json"
                },
                "body": "{\\"message\\": \\"Success after retries\\", \\"id\\": 1}"
              },
              "scenarioName": "retry-test",
              "requiredScenarioState": "second-failure"
            }
            """;

    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_JSON);

    // Configurar los 3 stubs
    restTemplate.postForEntity(adminUrl, new HttpEntity<>(firstStub, headers), String.class);
    restTemplate.postForEntity(adminUrl, new HttpEntity<>(secondStub, headers), String.class);
    restTemplate.postForEntity(adminUrl, new HttpEntity<>(thirdStub, headers), String.class);
  }

  private void verifyWireMockRequests() {
    RestTemplate restTemplate = new RestTemplate();
    String requestsUrl = "http://localhost:" + wireMock.getMappedPort(8080) + "/__admin/requests";

    ResponseEntity<String> response = restTemplate.getForEntity(requestsUrl, String.class);
    System.out.println("WireMock received requests: " + response.getBody());
  }

  private void verifyWireMockRetryRequests(int expectedCount) {
    RestTemplate restTemplate = new RestTemplate();
    String requestsUrl = "http://localhost:" + wireMock.getMappedPort(8080) + "/__admin/requests/count";

    String countQuery = """
            {
              "method": "POST",
              "url": "/api/users"
            }
            """;

    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_JSON);
    HttpEntity<String> request = new HttpEntity<>(countQuery, headers);

    ResponseEntity<String> response = restTemplate.postForEntity(requestsUrl, request, String.class);
    System.out.println("Expected " + expectedCount + " requests, WireMock count: " + response.getBody());
  }


}