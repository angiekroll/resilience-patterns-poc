package com.resiliencepatterns.poc.infrastructure.clients.userservice.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record UserDto(
    Long id,
    String name,
    String email
) {

}
