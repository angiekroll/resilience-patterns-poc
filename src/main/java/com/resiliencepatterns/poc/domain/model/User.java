/**
 * Copyright 2025, Company. All rights reserved Date: 3/10/25
 */
package com.resiliencepatterns.poc.domain.model;

import java.time.LocalDateTime;

/**
 *
 * @author angiekroll@gmail.com - Ángela Carolina Castillo Rodríguez.
 * @version - 1.0.0
 * @since - 1.0.0
 */
public class User {

  private final UserId id;
  private final UserEmail email;
  private final String name;
  private final LocalDateTime createdAt;

  public User(UserId id, UserEmail email, String name) {
    this.id = id;
    this.email = email;
    this.name = name;
    this.createdAt = LocalDateTime.now();
  }


  public UserId getId() {
    return id;
  }

  public UserEmail getEmail() {
    return email;
  }

  public String getName() {
    return name;
  }

  public LocalDateTime getCreatedAt() {
    return createdAt;
  }

  /**
   * 🎯 LÓGICA DE DOMINIO - Validación de negocio
   */
  public boolean isValidForProcessing() {
    return name != null && !name.trim().isEmpty() && email.isValid();
  }

  public User register() {
    // Lógica de negocio para registrar usuario
    return this;
  }

}