package com.example.foodchain.payments.mapper;

import com.example.foodchain.payments.dto.PaymentResponse;
import com.example.foodchain.payments.entity.Payment;
import org.springframework.stereotype.Component;

@Component
public class PaymentMapper {

    public PaymentResponse toResponse(Payment payment) {
        return new PaymentResponse(
                payment.getId(),
                payment.getOrderId(),
                payment.getMethod(),
                payment.getStatus(),
                payment.getAmount(),
                payment.getReference(),
                payment.getCreatedAt());
    }
}
