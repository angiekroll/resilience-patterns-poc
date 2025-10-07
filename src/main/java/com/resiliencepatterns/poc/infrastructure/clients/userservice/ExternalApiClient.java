/**
 * Copyright 2025, Company. All rights reserved Date: 3/10/25
 */
package com.resiliencepatterns.poc.infrastructure.clients.userservice;

import com.resiliencepatterns.poc.infrastructure.clients.userservice.dto.UserDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

/**
 *
 * @author angiekroll@gmail.com - Ángela Carolina Castillo Rodríguez.
 * @version - 1.0.0
 * @since - 1.0.0
 */

@FeignClient(
    name = "external-api",
    url = "${external-api.base-url}"
)
public interface ExternalApiClient {

  @PostMapping("${external-api.endpoints.users}")
  UserDto createUser(@RequestBody UserDto user);

}