package com.resiliencepatterns.poc.domain.model;

import com.resiliencepatterns.poc.domain.exceptions.InvalidUserIdException;

// TODO: PARA MI ESTO ES SOBREINGENIERIA, NO HAY REGLA FUERTE DE NEGOCIO QUE AMERITE LA VALIDACION DE NULL, DEBIESE USAR EN EL MOMENTO QUE EL FLUJO LO AMERITE
public record UserId(Long value) {

  public UserId {
    if (value == null || value <= 0) {
      throw new InvalidUserIdException("User ID must be positive");
    }
  }

  public static UserId of(Long value) {
    return new UserId(value);
  }

}
