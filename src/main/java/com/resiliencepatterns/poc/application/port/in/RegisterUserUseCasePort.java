/**
 * Copyright 2025. All rights reserved Date: 3/10/25
 */
package com.resiliencepatterns.poc.application.port.in;

import com.resiliencepatterns.poc.application.dto.UserRegistrationResponse;
import com.resiliencepatterns.poc.application.dto.RegisterUserCommand;

/**
 *
 * @author angiekroll@gmail.com - Ángela Carolina Castillo Rodríguez.
 * @version - 1.0.0
 * @since - 1.0.0
 */
public interface RegisterUserUseCasePort {

  UserRegistrationResponse registerUser(RegisterUserCommand command);

}