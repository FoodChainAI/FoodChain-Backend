package com.example.foodchain.payments.service;

import com.example.foodchain.catalog.service.OfferService;
import com.example.foodchain.common.error.ConflictException;
import com.example.foodchain.common.error.NotFoundException;
import com.example.foodchain.orders.dto.OrderPaymentInfo;
import com.example.foodchain.orders.service.OrderService;
import com.example.foodchain.payments.entity.Payment;
import com.example.foodchain.payments.entity.PaymentMethod;
import com.example.foodchain.payments.entity.PaymentStatus;
import com.example.foodchain.payments.provider.PaymentProvider;
import com.example.foodchain.payments.provider.ProviderChargeRequest;
import com.example.foodchain.payments.provider.ProviderChargeResult;
import com.example.foodchain.payments.repository.PaymentRepository;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class PaymentService {

    private static final Logger log = LoggerFactory.getLogger(PaymentService.class);

    private final PaymentRepository paymentRepository;
    private final OrderService orderService;
    private final OfferService offerService;
    private final Map<String, PaymentProvider> providers;
    private final String activeProviderKey;

    public PaymentService(PaymentRepository paymentRepository, OrderService orderService, OfferService offerService,
                          List<PaymentProvider> providerList,
                          @Value("${foodchain.payments.provider:mock}") String activeProviderKey) {
        this.paymentRepository = paymentRepository;
        this.orderService = orderService;
        this.offerService = offerService;
        this.providers = providerList.stream().collect(Collectors.toMap(PaymentProvider::key, Function.identity()));
        this.activeProviderKey = activeProviderKey;
    }

    /**
     * Initiates payment for a CONFIRMEE order owned by {@code buyerId}.
     * <ul>
     *   <li>A_LA_LIVRAISON: settled immediately (stock reserved, order → PAYEE).</li>
     *   <li>MOBILE_MONEY: handed to the gateway; stays INITIE until the callback.</li>
     * </ul>
     */
    @Transactional
    public Payment initiate(UUID buyerId, UUID orderId, PaymentMethod method) {
        OrderPaymentInfo info = orderService.getPayableOrder(orderId, buyerId);
        if (paymentRepository.existsByOrderIdAndStatus(orderId, PaymentStatus.REUSSI)) {
            throw new ConflictException("ALREADY_PAID", "Cette commande a déjà été payée.", Map.of());
        }
        String reference = generateReference();
        Payment payment = paymentRepository.save(
                Payment.initiate(orderId, method, info.amount(), reference));

        if (method == PaymentMethod.A_LA_LIVRAISON) {
            // Cash on delivery: reserve stock now, collect on delivery.
            settle(payment);
        } else {
            PaymentProvider provider = activeProvider();
            ProviderChargeResult result = provider.charge(
                    new ProviderChargeRequest(payment.getId(), orderId, info.amount(), reference));
            log.info("Payment {} handed to provider '{}' -> {}", reference, provider.key(), result.status());
            if (result.status() == PaymentStatus.REUSSI) {
                settle(payment);
            } else if (result.status() == PaymentStatus.ECHOUE) {
                payment.markFailed();
                paymentRepository.save(payment);
            }
            // INITIE: awaits the asynchronous callback.
        }
        return payment;
    }

    /**
     * Gateway callback. On REUSSI, stock decrement + order PAYEE happen atomically
     * in {@link #settle}. On failure, no stock is touched and the order is untouched.
     */
    @Transactional
    public Payment handleCallback(String reference, PaymentStatus reportedStatus) {
        Payment payment = paymentRepository.findByReference(reference)
                .orElseThrow(() -> new NotFoundException("Paiement introuvable pour la référence " + reference + "."));
        if (!payment.isPending()) {
            throw new ConflictException("PAYMENT_NOT_PENDING",
                    "Ce paiement n'est plus en attente (statut: " + payment.getStatus() + ").",
                    Map.of("status", payment.getStatus().name()));
        }
        if (reportedStatus == PaymentStatus.REUSSI) {
            settle(payment);
        } else {
            payment.markFailed();
            paymentRepository.save(payment);
        }
        return payment;
    }

    /**
     * Atomic settlement: lock offers, decrement stock, move the order to PAYEE and
     * mark the payment REUSSI — all in one transaction. If stock is insufficient,
     * the whole transaction rolls back and nothing changes.
     */
    @Transactional
    protected void settle(Payment payment) {
        OrderPaymentInfo info = orderService.getSettlementInfo(payment.getOrderId());
        offerService.reserveAndDecrement(info.decrements());
        orderService.markPaid(payment.getOrderId());
        payment.markSucceeded();
        paymentRepository.save(payment);
    }

    private PaymentProvider activeProvider() {
        PaymentProvider provider = providers.get(activeProviderKey);
        if (provider == null) {
            throw new IllegalStateException("Fournisseur de paiement inconnu: " + activeProviderKey);
        }
        return provider;
    }

    private String generateReference() {
        return "PMT-" + UUID.randomUUID().toString().replace("-", "").substring(0, 20).toUpperCase();
    }
}
