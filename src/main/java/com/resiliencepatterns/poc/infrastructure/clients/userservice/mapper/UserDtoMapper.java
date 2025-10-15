/**
 * Copyright 2025. All rights reserved Date: 15/10/25
 */
package com.resiliencepatterns.poc.infrastructure.clients.userservice.mapper;

import com.resiliencepatterns.poc.domain.model.User;
import com.resiliencepatterns.poc.infrastructure.clients.userservice.dto.UserDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

/**
 *
 * @author angiekroll@gmail.com - Ángela Carolina Castillo Rodríguez.
 * @version - 1.0.0
 * @since - 1.0.0
 */

@Mapper(componentModel = "spring")
public interface UserDtoMapper {

  @Mapping(target = "id", source = "id.value")
  @Mapping(target = "email", source = "email.value")
  UserDto toDto(User user);

  @Mapping(target = "id", expression = "java(UserId.of(userDto.id()))")
  @Mapping(target = "email", expression = "java(UserEmail.of(userDto.email()))")
  User toDomain(UserDto userDto);

}