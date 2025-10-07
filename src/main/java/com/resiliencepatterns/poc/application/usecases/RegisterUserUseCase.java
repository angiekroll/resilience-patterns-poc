/**
 * Copyright 2025, Company. All rights reserved Date: 3/10/25
 */
package com.resiliencepatterns.poc.application.usecases;

import com.resiliencepatterns.poc.application.dto.UserRegistrationResponse;
import com.resiliencepatterns.poc.application.dto.RegisterUserCommand;
import com.resiliencepatterns.poc.domain.exceptions.UserRegistrationException;
import com.resiliencepatterns.poc.domain.model.User;
import com.resiliencepatterns.poc.domain.model.UserEmail;
import com.resiliencepatterns.poc.domain.model.UserId;
import com.resiliencepatterns.poc.domain.port.in.RegisterUserUseCasePort;
import com.resiliencepatterns.poc.domain.port.out.UserRegistrationPort;
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
public class RegisterUserUseCase implements RegisterUserUseCasePort {

  private static final Logger log = LoggerFactory.getLogger(RegisterUserUseCase.class);

  private final UserRegistrationPort userRegistrationPort;

  public RegisterUserUseCase(UserRegistrationPort userRegistrationPort) {
    this.userRegistrationPort = userRegistrationPort;
  }

  @Override
  public UserRegistrationResponse registerUser(RegisterUserCommand registerUserCommand) {
    log.info("[API:Validation] Validating user: {} ", registerUserCommand.userId());

    try {
      User user = createUserFromDto(registerUserCommand);

      if (!user.isValidForProcessing()) {
        throw new IllegalArgumentException("User data is invalid for processing");
      }
      user = user.register();

      User registeredUser = userRegistrationPort.registerUser(user);
      log.info("[API:Response] User registered successfully: {}", registeredUser.getId().value());

      return UserRegistrationResponse.success(registeredUser.getId().value(), "User processed successfully");

    } catch (IllegalArgumentException | NullPointerException e) {
      log.error("Domain validation error for user: {}", e.getMessage());
      throw new UserRegistrationException("Invalid user data", e);
    }
  }

  private User createUserFromDto(RegisterUserCommand dto) {
    return new User(
        UserId.of(dto.userId()),
        UserEmail.of(dto.email()),
        dto.name()
    );
  }

}