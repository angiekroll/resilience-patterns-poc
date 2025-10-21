package com.resiliencepatterns.poc.infrastructure.adapters.in.http.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

@Schema(
    name = "UserRegistrationRequest",
    description = "User registration request payload",
    example = """
        {
          "userId": 12345,
          "name": "Angie Doe",
          "email": "angie.doe@example.com"
        }
        """
)
public record UserRegistrationRequest(
    @NotNull(message = "User ID is required")
    @Positive(message = "User ID must be positive")
    Long userId,

    @NotBlank(message = "Name is required")
    String name,

    @NotBlank(message = "Email is required")
    @Email(message = "Email must be valid")
    String email
) {}