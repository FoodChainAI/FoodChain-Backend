package com.example.foodchain.payments.provider;

import java.math.BigDecimal;
import java.util.UUID;

/** Data handed to a {@link PaymentProvider} to request a charge. */
public record ProviderChargeRequest(
        UUID paymentId,
        UUID orderId,
        BigDecimal amount,
        String reference
) {
}
