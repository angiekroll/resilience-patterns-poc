/**
 * Copyright 2025, Company. All rights reserved Date: 6/10/25
 */
package com.resiliencepatterns.poc.infrastructure.adapters.out.persistence;

import com.resiliencepatterns.poc.application.port.out.UserPersistencePort;
import org.springframework.stereotype.Repository;

/**
 *
 * @author angiekroll@gmail.com - Ángela Carolina Castillo Rodríguez.
 * @version - 1.0.0
 * @since - 1.0.0
 */

/*
@Repository
public class UserJpaAdapter implements UserPersistencePort {

  private final UserJpaRepository repository;
  private final UserEntityMapper mapper;

  public UserJpaAdapter(UserJpaRepository repository, UserEntityMapper mapper) {
    this.repository = repository;
    this.mapper = mapper;
  }

  @Override
  public User save(User user) {
    UserEntity entity = mapper.toEntity(user);
    UserEntity savedEntity = repository.save(entity);
    return mapper.toDomain(savedEntity);
  }

}*/
