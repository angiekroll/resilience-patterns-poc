/**
 * Copyright 2025, Company. All rights reserved Date: 17/10/25
 */
package com.resiliencepatterns.poc.infrastructure.exceptions;

/**
 *
 * @author angiekroll@gmail.com - Ángela Carolina Castillo Rodríguez.
 * @version - 1.0.0
 * @since - 1.0.0
 */
public abstract class InfrastructureException extends RuntimeException {

  protected InfrastructureException(String message, Throwable cause) {
    super(message, cause);
  }

  protected InfrastructureException(String message) {
    super(message);
  }

}