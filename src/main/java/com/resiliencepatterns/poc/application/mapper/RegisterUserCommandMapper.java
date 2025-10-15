/**
 * Copyright 2025, Company. All rights reserved Date: 15/10/25
 */
package com.resiliencepatterns.poc.application.mapper;

import com.resiliencepatterns.poc.application.dto.RegisterUserCommand;
import com.resiliencepatterns.poc.domain.model.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

/**
 *
 * @author angiekroll@gmail.com - Ángela Carolina Castillo Rodríguez.
 * @version - 1.0.0
 * @since - 1.0.0
 */

@Mapper(componentModel = "spring")
public interface RegisterUserCommandMapper {

  @Mapping(target = "id", expression = "java(UserId.of(command.userId()))")
  @Mapping(target = "email", expression = "java(UserEmail.of(command.email()))")
  @Mapping(target = "name", source = "name")
  User toDomain(RegisterUserCommand command);

}