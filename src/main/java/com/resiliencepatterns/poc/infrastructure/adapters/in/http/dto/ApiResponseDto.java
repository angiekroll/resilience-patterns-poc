package com.resiliencepatterns.poc.infrastructure.adapters.in.http.dto;

public record ApiResponseDto<T>(
    boolean success,
    T data,
    String message,
    long timestamp
) {

  public static <T> ApiResponseDto<T> success(T data) {
    return new ApiResponseDto<>(true, data, "Success", System.currentTimeMillis());
  }

  public static <T> ApiResponseDto<T> error(T data, String message) {
    return new ApiResponseDto<>(false, data, message, System.currentTimeMillis());
  }

}
