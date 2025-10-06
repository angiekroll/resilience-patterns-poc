/**
 * Copyright 2025. All rights reserved Date: 3/10/25
 */
package com.resiliencepatterns.poc.domain.port.out;

import com.resiliencepatterns.poc.domain.model.User;
import com.resiliencepatterns.poc.domain.model.UserId;

/**
 *
 * @author angiekroll@gmail.com - Ángela Carolina Castillo Rodríguez.
 * @version - 1.0.0
 * @since - 1.0.0
 */
public interface UserRegistrationPort {

  User registerUser(User user);

  User getUserById(UserId userId);

}