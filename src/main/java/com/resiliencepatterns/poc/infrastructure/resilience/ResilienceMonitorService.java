/**
 * Copyright 2025, Company. All rights reserved Date: 9/10/25
 */
package com.resiliencepatterns.poc.infrastructure.resilience;

import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;
import io.github.resilience4j.ratelimiter.RateLimiterRegistry;
import io.github.resilience4j.retry.RetryRegistry;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 *
 * @author angiekroll@gmail.com - Ángela Carolina Castillo Rodríguez.
 * @version - 1.0.0
 * @since - 1.0.0
 */

@Component
@Slf4j
public class ResilienceMonitorService {

  private final CircuitBreakerRegistry circuitBreakerRegistry;
  private final RetryRegistry retryRegistry;
  private final RateLimiterRegistry rateLimiterRegistry;

  public ResilienceMonitorService(CircuitBreakerRegistry circuitBreakerRegistry,
      RetryRegistry retryRegistry, RateLimiterRegistry rateLimiterRegistry) {
    this.circuitBreakerRegistry = circuitBreakerRegistry;
    this.retryRegistry = retryRegistry;
    this.rateLimiterRegistry = rateLimiterRegistry;
  }

  @PostConstruct
  public void setupEventListeners() {
    log.debug("[RESILIENCE] Configuring automatic listeners");
    setupCircuitBreakerListeners();
    setupRetryListeners();
    setupRateLimiterListeners();

  }

  //TODO: los emoji son solo para poder visualizar mejor los puntos principales en el log en desarrollo, pero deben quitarse.
  private void setupCircuitBreakerListeners() {
    circuitBreakerRegistry.circuitBreaker("external-api")
        .getEventPublisher()
        .onStateTransition(event ->
            log.warn("🔥 [CIRCUIT BREAKER] {} → {}",
                event.getStateTransition().getFromState(),
                event.getStateTransition().getToState()));
  }

  private void setupRetryListeners() {
    retryRegistry.retry("external-api")
        .getEventPublisher()
        .onRetry(event ->
            log.warn("🔥 [RETRY] Intento {} - Exception: {}",
                event.getNumberOfRetryAttempts(),
                event.getLastThrowable().getClass().getSimpleName()));
  }

  private void setupRateLimiterListeners() {
    var rateLimiter = rateLimiterRegistry.rateLimiter("external-api");

    rateLimiter.getEventPublisher()
        .onFailure(event ->
            log.warn("⚠️ [RATE LIMITER] Permission denied - Limit reached"));

    rateLimiter.getEventPublisher()
        .onSuccess(event ->
            log.debug("✅ [RATE LIMITER] Permission granted - Slot available"));

    rateLimiter.getEventPublisher()
        .onEvent(event -> {
          if (event.getEventType().toString().equals("FAILED_ACQUIRE")) {
            log.warn("⏳ [RATE LIMITER] Request waiting for available slot...");
          }
        });
  }

  public void logCircuitBreakerState(String circuitBreakerName) {
    var circuitBreaker = circuitBreakerRegistry.circuitBreaker(circuitBreakerName);
    log.debug("ℹ️ [Circuit Breaker] Current status: {}", circuitBreaker.getState());
  }

}