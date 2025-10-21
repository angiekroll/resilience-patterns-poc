/**
 * Copyright 2025, Company. All rights reserved Date: 3/10/25
 */
package com.resiliencepatterns.poc.infrastructure.adapters.in.http;

import com.resiliencepatterns.poc.application.dto.RegisterUserCommand;
import com.resiliencepatterns.poc.application.dto.UserRegistrationResponse;
import com.resiliencepatterns.poc.application.port.in.RegisterUserUseCasePort;
import com.resiliencepatterns.poc.infrastructure.adapters.in.http.dto.ApiResponseDto;
import com.resiliencepatterns.poc.infrastructure.adapters.in.http.dto.UserRegistrationRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
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
//@Tag(name = "Users", description = "User management operations with resilience patterns")
public class UserController implements UserApi{

  private final RegisterUserUseCasePort registerUserUseCasePort;

  public UserController(RegisterUserUseCasePort registerUserUseCasePort) {
    this.registerUserUseCasePort = registerUserUseCasePort;
  }


  @PostMapping()
  // TODO: eliminar codigo comentado, es solo para mostrar como ejemplo
/*  @Operation(
      summary = "Register a new user",
      description = "Registers a new user in the system with resilience patterns applied"
  )
  @ApiResponses(value = {
      @ApiResponse(responseCode = "201", description = "User successfully registered", content = @Content(
          mediaType = "application/json",
          schema = @Schema(implementation = ApiResponseDto.class)
      )),
      @ApiResponse(responseCode = "400", description = "Invalid input data"),
      @ApiResponse(responseCode = "500", description = "Internal server error")
  })*/
  public ResponseEntity<ApiResponseDto<UserRegistrationResponse>> registerUser(
      @Valid @RequestBody UserRegistrationRequest request) {
    log.info("[API:Request] Request received: {}", request.userId());

    RegisterUserCommand command = RegisterUserCommand.create(request.userId(), request.name(),
        request.email());

    UserRegistrationResponse result = registerUserUseCasePort.registerUser(command);

    return ResponseEntity
        .status(HttpStatus.CREATED)
        .body(ApiResponseDto.success(result));
  }

}