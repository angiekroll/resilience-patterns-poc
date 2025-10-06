/**
 * Copyright 2025. All rights reserved Date: 6/10/25
 */
package com.resiliencepatterns.poc.domain.port.out;

import com.resiliencepatterns.poc.domain.model.User;
import com.resiliencepatterns.poc.domain.model.UserId;
import java.util.Optional;

/**
 *
 * @author angiekroll@gmail.com - Ángela Carolina Castillo Rodríguez.
 * @version - 1.0.0
 * @since - 1.0.0
 */
public interface UserPersistencePort {

  User save(User user);

  Optional<User> findById(UserId userId);

  void deleteById(UserId userId);

}