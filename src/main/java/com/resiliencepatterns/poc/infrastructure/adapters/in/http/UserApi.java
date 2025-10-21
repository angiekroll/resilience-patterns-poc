/**
 * Copyright 2025, Company. All rights reserved Date: 20/10/25
 */
package com.resiliencepatterns.poc.infrastructure.adapters.in.http;

import com.resiliencepatterns.poc.application.dto.UserRegistrationResponse;
import com.resiliencepatterns.poc.infrastructure.adapters.in.http.dto.ApiResponseDto;
import com.resiliencepatterns.poc.infrastructure.adapters.in.http.dto.UserRegistrationRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.headers.Header;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

/**
 *
 * @author angiekroll@gmail.com - Ángela Carolina Castillo Rodríguez.
 * @version - 1.0.0
 * @since - 1.0.0
 */

@Tag(
    name = "Users",
    description = "User management operations with enterprise resilience patterns"
)
public interface UserApi {

  @PostMapping
  @Operation(
      summary = "Register a new user",
      description = """
          Registers a new user in the system with enterprise-grade resilience patterns applied.
          
          **Resilience Features:**
          - Circuit Breaker protection for external API calls
          - Rate limiting to prevent abuse
          - Retry mechanism with exponential backoff
          - Graceful degradation on external service failures
          """,
      parameters = {
          @Parameter(
              name = "X-Correlation-ID",
              description = "Request correlation ID for distributed tracing",
              in = ParameterIn.HEADER,
              required = false,
              example = "abc123-def456"
          )
      }
  )
  @ApiResponses(value = {
      @ApiResponse(
          responseCode = "201",
          description = "User successfully registered",
          headers = {
              @Header(
                  name = "X-Correlation-ID",
                  description = "Request correlation ID",
                  example = "abc123-def456"
              )
          },
          content = @Content(
              mediaType = "application/json",
              schema = @Schema(implementation = ApiResponseDto.class),
              examples = @ExampleObject(
                  name = "Successful registration",
                  summary = "User registered successfully",
                  description = "Example of successful user registration response",
                  value = """
                      {
                        "success": true,
                        "data": {
                          "success": true,
                          "userId": 12345,
                          "userName": "John Doe",
                          "userEmail": "john.doe@example.com",
                          "message": "User registered successfully",
                          "timestamp": "2025-01-03T10:30:45.123Z"
                        },
                        "message": "Success",
                        "timestamp": 1704267045123
                      }
                      """
              )
          )
      ),
      @ApiResponse(
          responseCode = "400",
          description = "Bad Request - Invalid input data",
          content = @Content(
              mediaType = "application/json",
              schema = @Schema(implementation = ApiResponseDto.class),
              examples = {
                  @ExampleObject(
                      name = "Validation errors",
                      summary = "Multiple validation errors",
                      description = "Example when multiple fields have validation errors",
                      value = """
                          {
                            "success": false,
                            "data": null,
                            "message": "User ID is required, Email must be valid, Name is required",
                            "timestamp": 1704267045123
                          }
                          """
                  ),
                  @ExampleObject(
                      name = "Invalid email format",
                      summary = "Domain validation error",
                      description = "Example when email format is invalid (InvalidEmailFormatException)",
                      value = """
                          {
                            "success": false,
                            "data": null,
                            "message": "Invalid email format",
                            "timestamp": 1704267045123
                          }
                          """
                  ),
                  @ExampleObject(
                      name = "Invalid user ID",
                      summary = "Domain validation error",
                      description = "Example when user ID is invalid (InvalidUserIdException)",
                      value = """
                          {
                            "success": false,
                            "data": null,
                            "message": "Invalid user ID",
                            "timestamp": 1704267045123
                          }
                          """
                  )
              }
          )
      ),
      @ApiResponse(
          responseCode = "500",
          description = "Internal Server Error",
          content = @Content(
              mediaType = "application/json",
              schema = @Schema(implementation = ApiResponseDto.class),
              examples = {
                  @ExampleObject(
                      name = "Internal server error",
                      summary = "Unexpected server error",
                      description = "Example of internal server error response",
                      value = """
                          {
                            "success": false,
                            "data": "abc123-def456",
                            "message": "An unexpected error occurred",
                            "timestamp": 1704267045123
                          }
                          """
                  ),
                  @ExampleObject(
                      name = "Application error",
                      summary = "Application layer error",
                      description = "Example of application exception response",
                      value = """
                          {
                            "success": false,
                            "data": null,
                            "message": "Internal processing error",
                            "timestamp": 1704267045123
                          }
                          """
                  )
              }
          )
      ),
      @ApiResponse(
          responseCode = "503",
          description = "Service Unavailable - Infrastructure error",
          content = @Content(
              mediaType = "application/json",
              schema = @Schema(implementation = ApiResponseDto.class),
              examples = @ExampleObject(
                  name = "Infrastructure error",
                  summary = "External service unavailable",
                  description = "Example when infrastructure services are unavailable",
                  value = """
                      {
                        "success": false,
                        "data": null,
                        "message": "Service temporarily unavailable",
                        "timestamp": 1704267045123
                      }
                      """
              )
          )
      )
  })
  ResponseEntity<ApiResponseDto<UserRegistrationResponse>> registerUser(
      @Valid @RequestBody UserRegistrationRequest request
  );

}