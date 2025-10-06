package com.resiliencepatterns.poc.application.dto;

import java.time.LocalDateTime;

public record UserRegistrationResponse(
    boolean success,
    Long userId,
    String message,
    LocalDateTime timestamp
) {

  public static UserRegistrationResponse success(Long userId, String message) {
    return new UserRegistrationResponse(true, userId, message, LocalDateTime.now());
  }

  public static UserRegistrationResponse failure(Long userId, String message) {
    return new UserRegistrationResponse(false, userId, message, LocalDateTime.now());
  }

}
