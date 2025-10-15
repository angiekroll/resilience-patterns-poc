package com.resiliencepatterns.poc.application.dto;

import com.resiliencepatterns.poc.domain.model.User;
import java.time.LocalDateTime;

public record UserRegistrationResponse(
    boolean success,
    Long userId,
    String userName,
    String userEmail,
    String message,
    LocalDateTime timestamp
) {

  public static UserRegistrationResponse success(User user, String message) {
    return new UserRegistrationResponse(true, user.getId().value(), user.getName(),       // ✅ Incluir nombre (con modificación fallback)
        user.getEmail().value(), message, LocalDateTime.now());
  }

  public static UserRegistrationResponse failure(User user, String message) {
    return new UserRegistrationResponse(false, user.getId().value(), user.getName(),       // ✅ Incluir nombre (con modificación fallback)
        user.getEmail().value(), message, LocalDateTime.now());
  }

}
