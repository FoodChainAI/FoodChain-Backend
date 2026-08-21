package com.example.foodchain.payments.provider;

import com.example.foodchain.payments.entity.PaymentStatus;

/**
 * Outcome of asking the gateway to charge.
 * {@code status == INITIE} means the charge is pending and the gateway will
 * confirm asynchronously via the callback endpoint.
 */
public record ProviderChargeResult(
        PaymentStatus status,
        String providerReference
) {
    public static ProviderChargeResult pending(String providerReference) {
        return new ProviderChargeResult(PaymentStatus.INITIE, providerReference);
    }
}
