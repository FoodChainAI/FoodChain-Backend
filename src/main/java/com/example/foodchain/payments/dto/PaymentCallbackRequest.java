package com.example.foodchain.payments.dto;

import com.example.foodchain.payments.entity.PaymentStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/** Payload posted by the payment gateway to confirm or reject a charge. */
public record PaymentCallbackRequest(
        @NotBlank String reference,
        @NotNull PaymentStatus status
) {
}
