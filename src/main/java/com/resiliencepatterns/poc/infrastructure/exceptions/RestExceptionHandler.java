/**
 * Copyright 2025, Company. All rights reserved Date: 6/10/25
 */
package com.resiliencepatterns.poc.infrastructure.exceptions;

import com.resiliencepatterns.poc.domain.exceptions.UserRegistrationException;
import com.resiliencepatterns.poc.infrastructure.adapters.in.http.dto.ApiResponseDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 *
 * @author angiekroll@gmail.com - Ángela Carolina Castillo Rodríguez.
 * @version - 1.0.0
 * @since - 1.0.0
 */

@RestControllerAdvice
@Slf4j
public class RestExceptionHandler {

  @ExceptionHandler(IllegalArgumentException.class)
  public ResponseEntity<ApiResponseDto<Void>> handleValidationError(IllegalArgumentException ex) {
    log.warn("Validation error: {}", ex.getMessage());
    return ResponseEntity
        .badRequest()
        .body(ApiResponseDto.error(null, ex.getMessage()));
  }

  @ExceptionHandler({UserRegistrationException.class, NullPointerException.class})
  public ResponseEntity<ApiResponseDto<Void>> handleDomainError(RuntimeException ex) {
    log.error("Domain error: {}", ex.getMessage(), ex);
    return ResponseEntity
        .status(HttpStatus.SERVICE_UNAVAILABLE)
        .body(ApiResponseDto.error(null, "Service temporarily unavailable"));
  }

// todo: en caso de manejar las excepciones de resilencia aquí
/*
  // 2. RestExceptionHandler maneja las excepciones de dominio de resiliencia
  @ExceptionHandler(CircuitBreakerOpenException.class)
  public ResponseEntity<ApiResponseDto<Void>> handleCircuitBreakerOpen(CircuitBreakerOpenException ex) {
    log.warn("Circuit breaker activated: {}", ex.getMessage());
    return ResponseEntity
        .status(HttpStatus.SERVICE_UNAVAILABLE)
        .body(ApiResponseDto.error("CIRCUIT_BREAKER_OPEN", "Service temporarily unavailable"));
  }

  @ExceptionHandler(RateLimitExceededException.class)
  public ResponseEntity<ApiResponseDto<Void>> handleRateLimit(RateLimitExceededException ex) {
    log.warn("Rate limit exceeded: {}", ex.getMessage());
    return ResponseEntity
        .status(HttpStatus.TOO_MANY_REQUESTS)
        .body(ApiResponseDto.error("RATE_LIMIT_EXCEEDED", "Too many requests"));
  }*/

}