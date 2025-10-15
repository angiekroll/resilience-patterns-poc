/**
 * Copyright 2025. All rights reserved Date: 3/10/25
 */
package com.resiliencepatterns.poc.application.port.out;

import com.resiliencepatterns.poc.domain.model.User;

/**
 *
 * @author angiekroll@gmail.com - Ángela Carolina Castillo Rodríguez.
 * @version - 1.0.0
 * @since - 1.0.0
 */
public interface UserRegistrationPort {

  User registerUser(User user);

}