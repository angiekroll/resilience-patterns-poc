/**
 * Copyright 2025, Company. All rights reserved Date: 6/10/25
 */
package com.resiliencepatterns.poc.infrastructure.adapters.out.persistence;

import com.resiliencepatterns.poc.application.port.out.UserRepository;
import com.resiliencepatterns.poc.domain.model.User;
import com.resiliencepatterns.poc.domain.model.UserId;
import com.resiliencepatterns.poc.infrastructure.exceptions.UserPersistenceException;
import com.resiliencepatterns.poc.infrastructure.persistence.entity.UserEntity;
import com.resiliencepatterns.poc.infrastructure.persistence.mapper.UserEntityMapper;
import com.resiliencepatterns.poc.infrastructure.persistence.repository.UserJpaRepository;
import java.util.Optional;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Repository;

/**
 *
 * @author angiekroll@gmail.com - Ángela Carolina Castillo Rodríguez.
 * @version - 1.0.0
 * @since - 1.0.0
 */

@Repository
public class UserRepositoryImpl implements UserRepository {

  private final UserJpaRepository repository;
  private final UserEntityMapper mapper;

  public UserRepositoryImpl(UserJpaRepository repository, UserEntityMapper mapper) {
    this.repository = repository;
    this.mapper = mapper;
  }

  @Override
  public User save(User user) {
    try {
      UserEntity entity = mapper.toEntity(user);
      UserEntity savedEntity = repository.save(entity);
      return mapper.toDomain(savedEntity);
    } catch (DataIntegrityViolationException e) {
      throw new UserPersistenceException("Failed to save user due to constraint violation", e);
    } catch (Exception e) {
      throw new UserPersistenceException("Failed to save user", e);
    }

  }

  @Override
  public Optional<User> findById(UserId userId) {
    try {
      return repository.findById(userId.value())
          .map(mapper::toDomain);
    } catch (Exception e) {
      throw new UserPersistenceException("Failed to find user by ID: " + userId.value(), e);
    }
  }

  @Override
  public void deleteById(UserId userId) {
    try {
      repository.deleteById(userId.value());
    } catch (Exception e) {
      throw new UserPersistenceException("Failed to delete user with ID: " + userId.value(), e);
    }
  }

}
