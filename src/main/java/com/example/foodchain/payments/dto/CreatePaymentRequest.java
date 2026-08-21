package com.example.foodchain.payments.dto;

import com.example.foodchain.payments.entity.PaymentMethod;
import jakarta.validation.constraints.NotNull;
import java.util.UUID;

public record CreatePaymentRequest(
        @NotNull UUID orderId,
        @NotNull PaymentMethod method
) {
}
