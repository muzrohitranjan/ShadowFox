package com.ecommerce.notification.config;

import org.springframework.amqp.core.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitConfig {
    
    @Bean
    public TopicExchange paymentExchange() {
        return new TopicExchange("payment.exchange");
    }
    
    @Bean
    public Queue paymentSuccessQueue() {
        return QueueBuilder.durable("payment.success.queue").build();
    }
    
    @Bean
    public Queue paymentFailedQueue() {
        return QueueBuilder.durable("payment.failed.queue").build();
    }
    
    @Bean
    public Binding paymentSuccessBinding() {
        return BindingBuilder
                .bind(paymentSuccessQueue())
                .to(paymentExchange())
                .with("payment.success");
    }
    
    @Bean
    public Binding paymentFailedBinding() {
        return BindingBuilder
                .bind(paymentFailedQueue())
                .to(paymentExchange())
                .with("payment.failed");
    }
}