/**
 * Copyright 2025, Company. All rights reserved Date: 6/10/25
 */
package com.resiliencepatterns.poc.infrastructure.exceptions;

import com.resiliencepatterns.poc.application.exceptions.ApplicationException;
import com.resiliencepatterns.poc.application.exceptions.UserRegistrationException;
import com.resiliencepatterns.poc.domain.exceptions.DomainException;
import com.resiliencepatterns.poc.domain.exceptions.InvalidEmailFormatException;
import com.resiliencepatterns.poc.domain.exceptions.InvalidUserIdException;
import com.resiliencepatterns.poc.infrastructure.adapters.in.http.dto.ApiResponseDto;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
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
public class GlobalExceptionHandler {

  // === EXCEPCIONES ESPECÍFICAS CRÍTICAS ===
  @ExceptionHandler(UserRegistrationException.class)
  public ResponseEntity<ApiResponseDto<Void>> handleUserRegistration(UserRegistrationException ex) {
    log.warn("Registration failed: {}", ex.getMessage());
    return ResponseEntity
        .status(HttpStatus.BAD_REQUEST)
        .body(ApiResponseDto.error(null, ex.getMessage()));
  }

  @ExceptionHandler(UserPersistenceException.class)
  public ResponseEntity<ApiResponseDto<Void>> handleUserPersistence(UserPersistenceException ex) {
    log.error("Database persistence error: {}", ex.getMessage(), ex);
    return ResponseEntity
        .status(HttpStatus.INTERNAL_SERVER_ERROR)
        .body(ApiResponseDto.error(null, "Failed to save user data"));
  }

  @ExceptionHandler(InvalidEmailFormatException.class)
  public ResponseEntity<ApiResponseDto<Void>> handleInvalidEmail(InvalidEmailFormatException ex) {
    log.warn("Invalid email format: {}", ex.getMessage());
    return ResponseEntity
        .badRequest()
        .body(ApiResponseDto.error(null, ex.getMessage()));
  }

  @ExceptionHandler(InvalidUserIdException.class)
  public ResponseEntity<ApiResponseDto<Void>> handleInvalidUserId(InvalidUserIdException ex) {
    log.warn("Invalid User ID: {}", ex.getMessage());
    return ResponseEntity
        .badRequest()
        .body(ApiResponseDto.error(null, ex.getMessage()));
  }


  // === VALIDACIONES COMUNES ===
  @ExceptionHandler(IllegalArgumentException.class)
  public ResponseEntity<ApiResponseDto<Void>> handleValidationError(IllegalArgumentException ex) {
    log.warn("Validation error: {}", ex.getMessage());
    return ResponseEntity
        .badRequest()
        .body(ApiResponseDto.error(null, ex.getMessage()));
  }

  @ExceptionHandler(NullPointerException.class)
  public ResponseEntity<ApiResponseDto<Void>> handleNullPointer(NullPointerException ex) {
    log.error("Null pointer error: {}", ex.getMessage(), ex);
    return ResponseEntity
        .status(HttpStatus.INTERNAL_SERVER_ERROR)
        .body(ApiResponseDto.error(null, ex.getMessage()));
  }

  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<ApiResponseDto<Void>> handleValidation(MethodArgumentNotValidException ex) {
    String errors = ex.getBindingResult().getFieldErrors().stream()
        .map(DefaultMessageSourceResolvable::getDefaultMessage)
        .collect(Collectors.joining(", "));

    log.warn("Validation failed: {}", errors);
    return ResponseEntity
        .badRequest()
        .body(ApiResponseDto.error(null, errors));
  }

// todo: en caso de manejar las excepciones de resilencia aquí
/*
// === PATRONES DE RESILIENCIA (ACTIVAR CUANDO SEA NECESARIO) ===
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


  // === EXCEPCIONES DE CAPA
  @ExceptionHandler(DomainException.class)
  public ResponseEntity<ApiResponseDto<Void>> handleDomainException(DomainException ex) {
    log.warn("Domain validation error: {}", ex.getMessage());
    return ResponseEntity
        .badRequest()
        .body(ApiResponseDto.error(null, ex.getMessage()));
  }

  @ExceptionHandler(ApplicationException.class)
  public ResponseEntity<ApiResponseDto<Void>> handleApplicationException(ApplicationException ex) {
    log.error("Application error: {}", ex.getMessage(), ex);
    return ResponseEntity
        .status(HttpStatus.INTERNAL_SERVER_ERROR)
        .body(ApiResponseDto.error(null, "Internal processing error"));
  }

  @ExceptionHandler(InfrastructureException.class)
  public ResponseEntity<ApiResponseDto<Void>> handleInfrastructureException(
      InfrastructureException ex) {
    log.error("Infrastructure error: {}", ex.getMessage(), ex);
    return ResponseEntity
        .status(HttpStatus.SERVICE_UNAVAILABLE)
        .body(ApiResponseDto.error(null, "Service temporarily unavailable"));
  }


  // === SEGURIDAD
  // Para cualquier otra excepción
  @ExceptionHandler(Exception.class)
  public ResponseEntity<ApiResponseDto<Void>> handleGenericException(Exception ex) {
    log.error("Unexpected error: {}", ex.getMessage(), ex);
    return ResponseEntity
        .status(HttpStatus.INTERNAL_SERVER_ERROR)
        .body(ApiResponseDto.error(null, "An unexpected error occurred"));
  }

}