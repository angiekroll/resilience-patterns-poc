/**
 * Copyright 2025, Company. All rights reserved Date: 3/10/25
 */
package com.resiliencepatterns.poc.infrastructure.adapters.out.rest;

import com.resiliencepatterns.poc.domain.model.User;
import com.resiliencepatterns.poc.domain.port.out.UserRegistrationPort;
import com.resiliencepatterns.poc.infrastructure.clients.userservice.ExternalApiClient;
import com.resiliencepatterns.poc.infrastructure.clients.userservice.dto.UserDto;
import com.resiliencepatterns.poc.infrastructure.resilience.ResilienceMonitorService;
import io.github.resilience4j.circuitbreaker.CallNotPermittedException;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.ratelimiter.RequestNotPermitted;
import io.github.resilience4j.ratelimiter.annotation.RateLimiter;
import io.github.resilience4j.retry.annotation.Retry;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 *
 * @author angiekroll@gmail.com - Ángela Carolina Castillo Rodríguez.
 * @version - 1.0.0
 * @since - 1.0.0
 */

@Service
@Slf4j
public class UserRegistrationAdapter implements UserRegistrationPort {

  private final ExternalApiClient externalApiClient;
  private final ResilienceMonitorService resilienceMonitorService;

  public UserRegistrationAdapter(ExternalApiClient externalApiClient,
      ResilienceMonitorService resilienceMonitorService) {
    this.externalApiClient = externalApiClient;
    this.resilienceMonitorService = resilienceMonitorService;
  }


  @Retry(name = "external-api", fallbackMethod = "registerUserFallback")
  @CircuitBreaker(name = "external-api")
  @RateLimiter(name = "external-api")
  @Override
  public User registerUser(User user) {
    log.info("[API:Execution] Starting user registration in external api, user: {}",
        user.getId().value());

    UserDto userDto = UserDto.fromDomain(user);
    UserDto result = externalApiClient.createUser(userDto);
    log.info("[API:Response] User registered successfully in external API: {}, and Mock: {}",
        user.getId().value(), result.id());
    return result.toDomain();
  }


  public User registerUserFallback(User user, Exception ex) {
    log.warn(
        "[Resilience: FALLBACK ACTIVATED] - User: {}, Thread: {}, Timestamp: {}, Exception: {}",
        user.getId().value(), Thread.currentThread().getName(), java.time.LocalDateTime.now(),
        ex.getClass().getSimpleName());

    if (ex instanceof RequestNotPermitted) {
      log.warn(
          "[Resilience: RATE LIMITER]: Request blocked for user: {}, - Too many requests, waiting 30s",
          user.getId().value());

      // VALIDAR SI REENVIAR MENSAJE A LA COLA CON DELAY

      return user;

    } else if (ex instanceof CallNotPermittedException) {
      log.warn("[Resilience: CIRCUIT BREAKER]: Circuit is OPEN for user: {} - Service unavailable",
          user.getId().value());
      throw new RuntimeException("Service circuit breaker open, Service temporarily unavailable",
          ex);

    } else {
      log.warn("[Resilience: RETRY EXHAUSTED]: All attempts failed for user: {}, - Final error: {}",
          user.getId().value(), ex.getMessage());
      throw new RuntimeException("Service unavailable after retries", ex);
    }
  }

}