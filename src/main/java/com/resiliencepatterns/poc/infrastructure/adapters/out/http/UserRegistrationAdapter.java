/**
 * Copyright 2025, Company. All rights reserved Date: 3/10/25
 */
package com.resiliencepatterns.poc.infrastructure.adapters.out.http;

import com.resiliencepatterns.poc.application.port.out.UserRegistrationPort;
import com.resiliencepatterns.poc.domain.model.User;
import com.resiliencepatterns.poc.infrastructure.clients.userservice.ExternalApiClient;
import com.resiliencepatterns.poc.infrastructure.clients.userservice.dto.UserDto;
import com.resiliencepatterns.poc.infrastructure.clients.userservice.mapper.UserDtoMapper;
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
  private final UserDtoMapper userDtoMapper;

  public UserRegistrationAdapter(ExternalApiClient externalApiClient, UserDtoMapper userDtoMapper) {
    this.externalApiClient = externalApiClient;
    this.userDtoMapper = userDtoMapper;
  }


  @Retry(name = "external-api", fallbackMethod = "registerUserFallback")
  @CircuitBreaker(name = "external-api")
  @RateLimiter(name = "external-api")
  @Override
  public User registerUser(User user) {
    log.info("[API:Execution] Starting user registration in external api, user: {}",
        user.getId().value());

    UserDto userDto = userDtoMapper.toDto(user);
    UserDto result = externalApiClient.createUser(userDto);
    log.info("[API:Response] User registered successfully in external API: {}, and Mock: {}",
        user.getId().value(), result.id());
    return userDtoMapper.toDomain(result);
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

      return user;

    } else if (ex instanceof CallNotPermittedException) {
      log.warn("[Resilience: CIRCUIT BREAKER]: Circuit is OPEN for user: {} - Service unavailable",
          user.getId().value());
      // VALIDAR SI REENVIAR MENSAJE A LA COLA CON DELAY
      /*throw new RuntimeException("Service circuit breaker open, Service temporarily unavailable",
          ex);*/
      return createDefaultResponse(user, "CIRCUIT_BREAKER_OPEN");

    } else {
      log.warn("[Resilience: RETRY EXHAUSTED]: All attempts failed for user: {}, - Final error: {}",
          user.getId().value(), ex.getMessage());
     /* throw new RuntimeException("Service unavailable after retries", ex);*/
      return createDefaultResponse(user, "SERVICE_UNAVAILABLE");
    }
  }

  private User createDefaultResponse(User originalUser, String reason) {
    return new User(
        originalUser.getId(),
        originalUser.getEmail(),
        originalUser.getName() + "_DEFAULT_" + reason + "_NOT_RESPONSE"
    );
  }

}