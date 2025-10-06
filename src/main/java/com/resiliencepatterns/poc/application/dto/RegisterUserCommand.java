package com.resiliencepatterns.poc.application.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record RegisterUserCommand(
    Long userId,
    String name,
    String email,
    String operation
) {

  public static RegisterUserCommand create(Long userId, String name, String email) {
    return new RegisterUserCommand(userId, name, email, "CREATE_USER");
  }

}
