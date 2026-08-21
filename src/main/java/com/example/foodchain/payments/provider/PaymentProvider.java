package com.example.foodchain.payments.provider;

/**
 * Abstraction over a Mobile Money gateway. Implementations talk to a concrete
 * provider (MTN MoMo, Orange Money, ...) or, in dev/tests, a mock.
 */
public interface PaymentProvider {

    /** Stable key used to select the active provider (e.g. "mock", "mtn"). */
    String key();

    /**
     * Requests a charge. Returning {@link ProviderChargeResult#pending} defers
     * settlement to an asynchronous callback carrying the same {@code reference}.
     */
    ProviderChargeResult charge(ProviderChargeRequest request);
}
