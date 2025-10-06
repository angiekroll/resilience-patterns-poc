/**
 * Copyright 2025, Company. All rights reserved Date: 3/10/25
 */
package com.resiliencepatterns.poc.infrastructure.adapters.in.rest;

import com.resiliencepatterns.poc.application.dto.UserRegistrationResponse;
import com.resiliencepatterns.poc.application.dto.RegisterUserCommand;
import com.resiliencepatterns.poc.domain.port.in.RegisterUserUseCasePort;
import com.resiliencepatterns.poc.infrastructure.adapters.in.rest.dto.ApiResponseDto;
import com.resiliencepatterns.poc.infrastructure.adapters.in.rest.dto.UserRegistrationRequest;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
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
@RestController
@RequestMapping("/api/v1/users")
@CrossOrigin(origins = "*")
public class UserController {

  private static final Logger log = LoggerFactory.getLogger(UserController.class);

  private final RegisterUserUseCasePort registerUserUseCasePort;

  public UserController(RegisterUserUseCasePort registerUserUseCasePort) {
    this.registerUserUseCasePort = registerUserUseCasePort;
  }


  @PostMapping("/process")
  public ResponseEntity<ApiResponseDto<UserRegistrationResponse>> registerUser(
      @Valid @RequestBody UserRegistrationRequest request) {
    log.info("REST request to process user: {}", request.userId());

    try {
      RegisterUserCommand messageDto = RegisterUserCommand.create(request.userId(), request.name(),
          request.email());

      UserRegistrationResponse result = registerUserUseCasePort.registerUser(messageDto);

      log.info("REST processing successful for user, via REST: {}", request.userId());
      return ResponseEntity.ok(ApiResponseDto.success(result));

    } catch (Exception e) {
      log.error("REST processing failed for user {}: {}", request.userId(), e.getMessage());

      UserRegistrationResponse errorResult = UserRegistrationResponse.failure(
          request.userId(),
          "Processing failed: " + e.getMessage()
      );

      return ResponseEntity.status(503) // Service Unavailable
          .body(ApiResponseDto.error(errorResult, "Processing failed: " + e.getMessage()));
    }
  }

}