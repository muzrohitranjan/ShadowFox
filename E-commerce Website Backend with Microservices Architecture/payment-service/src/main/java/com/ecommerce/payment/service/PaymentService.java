package com.ecommerce.payment.service;

import com.ecommerce.payment.dto.PaymentRequest;
import com.ecommerce.payment.entity.Payment;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class PaymentService {
    
    @Autowired
    private RedisTemplate<String, Object> redisTemplate;
    
    @Autowired
    private RabbitTemplate rabbitTemplate;
    
    public Payment processPayment(PaymentRequest request) {
        Payment payment = new Payment(
            request.getOrderId(),
            request.getUserId(),
            request.getAmount(),
            request.getPaymentMethod()
        );
        
        payment.setId(UUID.randomUUID().toString());
        
        // Simulate payment processing
        boolean paymentSuccess = simulatePaymentGateway(request);
        
        if (paymentSuccess) {
            payment.setStatus(Payment.PaymentStatus.SUCCESS);
            // Send payment success event
            rabbitTemplate.convertAndSend("payment.exchange", "payment.success", payment.getOrderId());
        } else {
            payment.setStatus(Payment.PaymentStatus.FAILED);
            // Send payment failed event
            rabbitTemplate.convertAndSend("payment.exchange", "payment.failed", payment.getOrderId());
        }
        
        // Store in Redis
        redisTemplate.opsForValue().set("payment:" + payment.getId(), payment);
        
        return payment;
    }
    
    private boolean simulatePaymentGateway(PaymentRequest request) {
        // Simulate payment processing - 90% success rate
        return Math.random() > 0.1;
    }
    
    public Payment getPaymentById(String paymentId) {
        return (Payment) redisTemplate.opsForValue().get("payment:" + paymentId);
    }
}