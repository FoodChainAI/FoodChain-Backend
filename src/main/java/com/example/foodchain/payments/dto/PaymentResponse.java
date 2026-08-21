package com.example.foodchain.payments.dto;

import com.example.foodchain.payments.entity.PaymentMethod;
import com.example.foodchain.payments.entity.PaymentStatus;
import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

public record PaymentResponse(
        UUID id,
        UUID orderId,
        PaymentMethod method,
        PaymentStatus status,
        BigDecimal amount,
        String reference,
        OffsetDateTime createdAt
) {
}
