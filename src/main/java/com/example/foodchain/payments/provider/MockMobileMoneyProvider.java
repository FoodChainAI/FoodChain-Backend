package com.example.foodchain.payments.provider;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * Deterministic Mobile Money provider for dev and tests. It "accepts" the charge
 * and returns pending — settlement is then driven by a callback (which the tests
 * or a simulated gateway POST to {@code /api/v1/payments/callback}).
 */
@Component
public class MockMobileMoneyProvider implements PaymentProvider {

    private static final Logger log = LoggerFactory.getLogger(MockMobileMoneyProvider.class);

    @Override
    public String key() {
        return "mock";
    }

    @Override
    public ProviderChargeResult charge(ProviderChargeRequest request) {
        log.info("[MOCK MoMo] charge requested amount={} reference={}", request.amount(), request.reference());
        // Pending: the gateway will confirm via the callback endpoint.
        return ProviderChargeResult.pending("MOCK-" + request.reference());
    }
}
