package com.ecommerce.notification.listener;

import com.ecommerce.notification.service.NotificationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class OrderEventListener {
    
    private static final Logger logger = LoggerFactory.getLogger(OrderEventListener.class);
    
    @Autowired
    private NotificationService notificationService;
    
    @RabbitListener(queues = "order.created.queue")
    public void handleOrderCreated(Long orderId) {
        logger.info("Received order created event for Order ID: {}", orderId);
        // In real implementation, fetch user details and send notification
        notificationService.sendOrderConfirmation(orderId, "user@example.com");
    }
    
    @RabbitListener(queues = "payment.success.queue")
    public void handlePaymentSuccess(Long orderId) {
        logger.info("Received payment success event for Order ID: {}", orderId);
        notificationService.sendPaymentConfirmation(orderId, "user@example.com", "SUCCESS");
    }
    
    @RabbitListener(queues = "payment.failed.queue")
    public void handlePaymentFailed(Long orderId) {
        logger.info("Received payment failed event for Order ID: {}", orderId);
        notificationService.sendPaymentConfirmation(orderId, "user@example.com", "FAILED");
    }
}