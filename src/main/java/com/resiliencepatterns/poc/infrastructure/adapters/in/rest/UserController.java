/**
 * Copyright 2025, Company. All rights reserved Date: 3/10/25
 */
package com.resiliencepatterns.poc.infrastructure.adapters.in.rest;

import com.resiliencepatterns.poc.application.dto.RegisterUserCommand;
import com.resiliencepatterns.poc.application.dto.UserRegistrationResponse;
import com.resiliencepatterns.poc.domain.port.in.RegisterUserUseCasePort;
import com.resiliencepatterns.poc.infrastructure.adapters.in.rest.dto.ApiResponseDto;
import com.resiliencepatterns.poc.infrastructure.adapters.in.rest.dto.UserRegistrationRequest;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 *
 * @author angiekroll@gmail.com - Ángela Carolina Castillo Rodríguez.
 * @version - 1.0.0
 * @since - 1.0.0
 */

@Slf4j
@RestController
@RequestMapping("/api/v1/users")
@Validated
public class UserController {

  private final RegisterUserUseCasePort registerUserUseCasePort;

  public UserController(RegisterUserUseCasePort registerUserUseCasePort) {
    this.registerUserUseCasePort = registerUserUseCasePort;
  }


  @PostMapping()
  public ResponseEntity<ApiResponseDto<UserRegistrationResponse>> registerUser(
      @Valid @RequestBody UserRegistrationRequest request) {
    log.info("[API:Request] Request received: {}", request.userId());

    RegisterUserCommand command = RegisterUserCommand.create(request.userId(), request.name(),
        request.email());

    UserRegistrationResponse result = registerUserUseCasePort.registerUser(command);

    log.info("[API:Response] User created successfully: {}", request.userId());
    return ResponseEntity
        .status(HttpStatus.CREATED)
        .body(ApiResponseDto.success(result));
  }

}