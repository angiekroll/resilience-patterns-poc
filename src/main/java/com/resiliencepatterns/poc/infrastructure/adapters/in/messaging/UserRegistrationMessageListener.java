/**
 * Copyright 2025, Company. All rights reserved Date: 3/10/25
 */
package com.resiliencepatterns.poc.infrastructure.adapters.in.messaging;

import com.resiliencepatterns.poc.application.dto.UserRegistrationResponse;
import com.resiliencepatterns.poc.application.dto.RegisterUserCommand;
import com.resiliencepatterns.poc.domain.port.in.RegisterUserUseCasePort;
import java.io.IOException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.support.AmqpHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;
import com.rabbitmq.client.Channel;

/**
 *
 * @author angiekroll@gmail.com - Ángela Carolina Castillo Rodríguez.
 * @version - 1.0.0
 * @since - 1.0.0
 */

@Component
public class UserRegistrationMessageListener {

  private static final Logger log = LoggerFactory.getLogger(UserRegistrationMessageListener.class);

  private final RegisterUserUseCasePort registerUserUseCasePort;

  public UserRegistrationMessageListener(RegisterUserUseCasePort registerUserUseCasePort) {
    this.registerUserUseCasePort = registerUserUseCasePort;
  }

  @RabbitListener(queues = "user.registration.queue")
  public void handleUserRegistrationRequest(
      @Payload RegisterUserCommand message,
      Channel channel,
      @Header(AmqpHeaders.DELIVERY_TAG) long deliveryTag) {
    log.info("Received message for user: {}", message.userId());

    try {
      // Procesar mensaje usando el caso de uso
      UserRegistrationResponse result = registerUserUseCasePort.registerUser(message);

      // ACK - Mensaje procesado exitosamente
      channel.basicAck(deliveryTag, false);
      log.info("Message processed and acknowledged: {}", message.userId());

    } catch (Exception e) {
      handleProcessingFailure(message, e, channel, deliveryTag);
    }
  }

  private void handleProcessingFailure(RegisterUserCommand message, Exception e,
      Channel channel, long deliveryTag) {
    try {
      String errorType = determineErrorType(e);

      if (shouldRequeue(errorType)) {
        // NACK con requeue - Para rate limit timeout
        channel.basicNack(deliveryTag, false, true);
        log.warn("Message requeued due to: {} - User: {}", errorType, message.userId());
      } else {
        // NACK sin requeue - Va a DLQ
        channel.basicNack(deliveryTag, false, false);
        log.error("Message sent to DLQ due to: {} - User: {}", errorType, message.userId());
      }

    } catch (IOException ioEx) {
      log.error("Error handling message failure", ioEx);
    }
  }

  private String determineErrorType(Exception e) {
    String message = e.getMessage();
    if (message.contains("Rate limit")) return "RATE_LIMIT";
    if (message.contains("Circuit breaker")) return "CIRCUIT_BREAKER";
    if (message.contains("retries")) return "RETRY_EXHAUSTED";
    return "UNKNOWN_ERROR";
  }

  private boolean shouldRequeue(String errorType) {
    // Solo requeue para rate limit timeout
    return "RATE_LIMIT".equals(errorType);
  }

}