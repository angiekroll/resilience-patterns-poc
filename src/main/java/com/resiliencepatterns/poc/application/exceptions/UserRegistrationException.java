/**
 * Copyright 2025, Company. All rights reserved Date: 7/10/25
 */
package com.resiliencepatterns.poc.application.exceptions;

/**
 *
 * @author angiekroll@gmail.com - Ángela Carolina Castillo Rodríguez.
 * @version - 1.0.0
 * @since - 1.0.0
 */
public class UserRegistrationException extends ApplicationException {

  public UserRegistrationException(String message, Throwable cause) {
    super(message, cause);
  }

  public UserRegistrationException(String message) {
    super(message);
  }

}