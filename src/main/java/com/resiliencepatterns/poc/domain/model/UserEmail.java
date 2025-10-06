/**
 * Copyright 2025, Company. All rights reserved Date: 3/10/25
 */
package com.resiliencepatterns.poc.domain.model;

import java.util.regex.Pattern;

/**
 *
 * @author angiekroll@gmail.com - Ángela Carolina Castillo Rodríguez.
 * @version - 1.0.0
 * @since - 1.0.0
 */
public record UserEmail(String value) {

  private static final Pattern EMAIL_PATTERN =
      Pattern.compile("^[A-Za-z0-9+_.-]+@(.+)$");

  public UserEmail {
    if (value == null || !EMAIL_PATTERN.matcher(value).matches()) {
      throw new IllegalArgumentException("Invalid email format");
    }
  }

  public boolean isValid() {
    return value != null && EMAIL_PATTERN.matcher(value).matches();
  }

  public static UserEmail of(String email) {
    return new UserEmail(email);
  }

}