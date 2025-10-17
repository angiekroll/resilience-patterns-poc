/**
 * Copyright 2025, Company. All rights reserved Date: 3/10/25
 */
package com.resiliencepatterns.poc.application.usecases;

import com.resiliencepatterns.poc.application.dto.UserRegistrationResponse;
import com.resiliencepatterns.poc.application.dto.RegisterUserCommand;
import com.resiliencepatterns.poc.application.mapper.UserMapper;
import com.resiliencepatterns.poc.application.port.in.RegisterUserUseCasePort;
import com.resiliencepatterns.poc.application.port.out.UserRegistrationPort;
import com.resiliencepatterns.poc.domain.exceptions.UserRegistrationException;
import com.resiliencepatterns.poc.domain.model.User;
import com.resiliencepatterns.poc.domain.model.UserEmail;
import com.resiliencepatterns.poc.domain.model.UserId;
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
public class RegisterUserUseCase implements
    RegisterUserUseCasePort {

  private final UserRegistrationPort userRegistrationPort;
  private final UserMapper userMapper;

  public RegisterUserUseCase(UserRegistrationPort userRegistrationPort, UserMapper userMapper) {
    this.userRegistrationPort = userRegistrationPort;
    this.userMapper = userMapper;
  }

  @Override
  public UserRegistrationResponse registerUser(RegisterUserCommand registerUserCommand) {
    log.info("[API:Validation] Validating user: {} ", registerUserCommand.userId());

    try {
      User user = userMapper.fromCommand(registerUserCommand); // Estrategia con mapper
     // User user = createUserFromDto(registerUserCommand);    // Estrategia mapeo directo
      if (!user.isValidForProcessing()) {
        log.error("Domain error validating user: {} ", registerUserCommand.userId());
        throw new IllegalArgumentException("User data is invalid for processing");
      }
      user = user.register();

      User registeredUser = userRegistrationPort.registerUser(user);

      return UserRegistrationResponse.success(registeredUser, "User processed successfully");

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