/**
 * Copyright 2025, Company. All rights reserved Date: 17/10/25
 */
package com.resiliencepatterns.poc.application.exceptions;

/**
 *
 * @author angiekroll@gmail.com - Ángela Carolina Castillo Rodríguez.
 * @version - 1.0.0
 * @since - 1.0.0
 */
public abstract class ApplicationException extends RuntimeException {

  protected ApplicationException(String message) {
    super(message);
  }

  protected ApplicationException(String message, Throwable cause) {
    super(message, cause);
  }

}