package com.example.foodchain.payments.provider;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * Skeleton of a real MTN MoMo (Collections API) provider. Wire the actual HTTP
 * calls where indicated. Activated only when {@code foodchain.payments.provider=mtn}.
 *
 * <p>Typical flow:
 * <ol>
 *   <li>Obtain an OAuth2 access token from the MoMo token endpoint.</li>
 *   <li>POST /collection/v1_0/requesttopay with the amount, payer MSISDN and an
 *       {@code X-Reference-Id} (our {@code reference}).</li>
 *   <li>MoMo confirms asynchronously; configure its webhook to hit our
 *       {@code /api/v1/payments/callback} with the reference and final status.</li>
 * </ol>
 */
@Component
public class MtnMomoPaymentProvider implements PaymentProvider {

    private static final Logger log = LoggerFactory.getLogger(MtnMomoPaymentProvider.class);

    private final String baseUrl;
    private final String subscriptionKey;
    private final String apiUser;
    private final String apiKey;

    public MtnMomoPaymentProvider(
            @Value("${foodchain.payments.mtn.base-url:https://sandbox.momodeveloper.mtn.com}") String baseUrl,
            @Value("${foodchain.payments.mtn.subscription-key:}") String subscriptionKey,
            @Value("${foodchain.payments.mtn.api-user:}") String apiUser,
            @Value("${foodchain.payments.mtn.api-key:}") String apiKey) {
        this.baseUrl = baseUrl;
        this.subscriptionKey = subscriptionKey;
        this.apiUser = apiUser;
        this.apiKey = apiKey;
    }

    @Override
    public String key() {
        return "mtn";
    }

    @Override
    public ProviderChargeResult charge(ProviderChargeRequest request) {
        log.info("[MTN MoMo] would call {}/collection/v1_0/requesttopay for reference={}", baseUrl, request.reference());
        // TODO: implement OAuth2 token retrieval + requesttopay HTTP call using a RestClient.
        //  - Authenticate with apiUser/apiKey and subscriptionKey.
        //  - Send X-Reference-Id = request.reference().
        //  - Return pending; final status arrives on the callback endpoint.
        if (subscriptionKey.isBlank()) {
            throw new IllegalStateException("MTN MoMo provider is not configured (missing subscription key).");
        }
        return ProviderChargeResult.pending(request.reference());
    }
}
