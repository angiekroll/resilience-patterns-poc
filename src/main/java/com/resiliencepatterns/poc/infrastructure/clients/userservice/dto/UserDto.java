package com.resiliencepatterns.poc.infrastructure.clients.userservice.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.resiliencepatterns.poc.domain.model.User;
import com.resiliencepatterns.poc.domain.model.UserEmail;
import com.resiliencepatterns.poc.domain.model.UserId;

@JsonIgnoreProperties(ignoreUnknown = true)
public record UserDto(
    Long id,
    String name,
    String email
) {

  public static UserDto fromDomain(User user) {
    return new UserDto(
        user.getId().value(),
        user.getName(),
        user.getEmail().value()
    );
  }

  public User toDomain() {
    return new User(
        UserId.of(id),
        UserEmail.of(email),
        name
    );
  }

}
