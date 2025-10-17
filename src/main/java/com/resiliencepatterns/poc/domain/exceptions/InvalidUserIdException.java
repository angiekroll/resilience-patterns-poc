/**
 * Copyright 2025, Company. All rights reserved Date: 17/10/25
 */
package com.resiliencepatterns.poc.domain.exceptions;

/**
 *
 * @author angiekroll@gmail.com - Ángela Carolina Castillo Rodríguez.
 * @version - 1.0.0
 * @since - 1.0.0
 */
public class InvalidUserIdException extends DomainException {

  public InvalidUserIdException(String message) {
    super(message);
  }

  public InvalidUserIdException(String message, Throwable cause) {
    super(message, cause);
  }

}