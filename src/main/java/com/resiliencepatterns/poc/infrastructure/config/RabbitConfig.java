/**
 * Copyright 2025, Company. All rights reserved Date: 3/10/25
 */
package com.resiliencepatterns.poc.infrastructure.config;

import org.springframework.amqp.core.AcknowledgeMode;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.QueueBuilder;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 *
 * @author angiekroll@gmail.com - Ángela Carolina Castillo Rodríguez.
 * @version - 1.0.0
 * @since - 1.0.0
 */

/**
 * CONFIGURACIÓN DE RABBITMQ - Colas y exchanges
 */
@Configuration
public class RabbitConfig {

  @Bean
  public Queue userRegistrationQueue() {
    return QueueBuilder.durable("user.registration.queue")
        .withArgument("x-dead-letter-exchange", "user.dlx")
        .withArgument("x-dead-letter-routing-key", "user.failed")
        .build();
  }

  @Bean
  public Queue userDeadLetterQueue() {
    return QueueBuilder.durable("user.dlq").build();
  }

  @Bean
  public DirectExchange userExchange() {
    return new DirectExchange("user.exchange");
  }

  @Bean
  public DirectExchange userDlx() {
    return new DirectExchange("user.dlx");
  }

  @Bean
  public Binding userRegistrationBinding() {
    return BindingBuilder
        .bind(userRegistrationQueue())
        .to(userExchange())
        .with("user.processing");
  }

  @Bean
  public Binding dlqBinding() {
    return BindingBuilder
        .bind(userDeadLetterQueue())
        .to(userDlx())
        .with("user.failed");
  }

  @Bean
  public Jackson2JsonMessageConverter messageConverter() {
    return new Jackson2JsonMessageConverter();
  }

  @Bean
  public SimpleRabbitListenerContainerFactory rabbitListenerContainerFactory(
      ConnectionFactory connectionFactory) {
    SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
    factory.setConnectionFactory(connectionFactory);
    factory.setMessageConverter(messageConverter());
    factory.setAcknowledgeMode(AcknowledgeMode.MANUAL);
    factory.setConcurrentConsumers(1);
    factory.setMaxConcurrentConsumers(2);
    factory.setPrefetchCount(1);
    return factory;
  }

}