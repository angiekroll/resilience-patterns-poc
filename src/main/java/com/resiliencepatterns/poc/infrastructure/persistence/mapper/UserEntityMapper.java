/**
 * Copyright 2025, Company. All rights reserved Date: 6/10/25
 */
package com.resiliencepatterns.poc.infrastructure.persistence.mapper;

import com.resiliencepatterns.poc.domain.model.User;
import com.resiliencepatterns.poc.domain.model.UserEmail;
import com.resiliencepatterns.poc.domain.model.UserId;
import com.resiliencepatterns.poc.infrastructure.persistence.entity.UserEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

/**
 *
 * @author angiekroll@gmail.com - Ángela Carolina Castillo Rodríguez.
 * @version - 1.0.0
 * @since - 1.0.0
 */
@Mapper(componentModel = "spring")
public interface UserEntityMapper {

  @Mapping(target = "id", source = "id.value")
  @Mapping(target = "email", source = "email.value")
  @Mapping(target = "name", source = "name")
  UserEntity toEntity(User user);

  @Mapping(target = "id", qualifiedByName = "mapToUserId")
  @Mapping(target = "email", qualifiedByName = "mapToUserEmail")
  @Mapping(target = "name", source = "name")
  User toDomain(UserEntity entity);

  @Named("mapToUserId")
  default UserId mapToUserId(Long id) {
    return UserId.of(id);
  }

  @Named("mapToUserEmail")
  default UserEmail mapToUserEmail(String email) {
    return UserEmail.of(email);
  }
}
