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
public class InvalidEmailFormatException extends DomainException {
  public InvalidEmailFormatException(String email) {
    super("Invalid email format: " + email);
  }

}