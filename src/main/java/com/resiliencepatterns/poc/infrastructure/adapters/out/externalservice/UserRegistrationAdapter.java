/**
 * Copyright 2025, Company. All rights reserved Date: 3/10/25
 */
package com.resiliencepatterns.poc.infrastructure.adapters.out.externalservice;

import com.resiliencepatterns.poc.domain.model.User;
import com.resiliencepatterns.poc.domain.model.UserId;
import com.resiliencepatterns.poc.domain.port.out.UserRegistrationPort;
import com.resiliencepatterns.poc.infrastructure.clients.userservice.ExternalApiClient;
import com.resiliencepatterns.poc.infrastructure.clients.userservice.dto.UserDto;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.ratelimiter.annotation.RateLimiter;
import io.github.resilience4j.retry.annotation.Retry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/**
 *
 * @author angiekroll@gmail.com - Ángela Carolina Castillo Rodríguez.
 * @version - 1.0.0
 * @since - 1.0.0
 */

@Service
public class UserRegistrationAdapter implements UserRegistrationPort {

  private static final Logger log = LoggerFactory.getLogger(UserRegistrationAdapter.class);

  private final ExternalApiClient externalApiClient;

  public UserRegistrationAdapter(ExternalApiClient externalApiClient) {
    this.externalApiClient = externalApiClient;
  }

  /**
   * METODO CON TODOS LOS PATRONES DE RESILIENCIA Orden: Rate Limiter → Circuit Breaker → Retry
   */
  @RateLimiter(name = "external-api", fallbackMethod = "createUserRateLimitFallback")
  @CircuitBreaker(name = "external-api", fallbackMethod = "createUserCircuitFallback")
  @Retry(name = "external-api", fallbackMethod = "createUserRetryFallback")
  @Override
  public User registerUser(User user) {
    log.info("Creating user in external API: {}", user.getId().value());

    UserDto userDto = UserDto.fromDomain(user);

    UserDto result = externalApiClient.createUser(userDto);
    log.info("User created successfully in external API: {}", result.id());

    return result.toDomain();
  }

  @Override
  public User getUserById(UserId userId) {
    log.info("Getting user from external API: {}", userId.value());

    UserDto result = externalApiClient.getUserById(userId.value());
    return result.toDomain();
  }

  /**
   * Fallback para Rate Limiter
   */
  public User createUserRateLimitFallback(User user, Exception ex) {
    log.warn("Rate limit exceeded for user: {}", user.getId().value());
    throw new RuntimeException("Rate limit exceeded - system overloaded", ex);
  }

  /**
   * Fallback para Circuit Breaker
   */
  public User createUserCircuitFallback(User user, Exception ex) {
    log.warn("Circuit breaker open for user: {}", user.getId().value());
    throw new RuntimeException("Circuit breaker open - external API unavailable", ex);
  }

  /**
   * Fallback para Retry
   */
  public User createUserRetryFallback(User user, Exception ex) {
    log.error("All retries failed for user: {}", user.getId().value());
    throw new RuntimeException("All retries exhausted - external API error", ex);
  }

}