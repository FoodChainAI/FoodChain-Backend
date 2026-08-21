package com.example.foodchain.integration;

import com.example.foodchain.AbstractIntegrationTest;
import com.example.foodchain.catalog.entity.Offer;
import com.example.foodchain.catalog.entity.Product;
import com.example.foodchain.catalog.repository.OfferRepository;
import com.example.foodchain.catalog.repository.ProductRepository;
import com.example.foodchain.orders.dto.CreateOrderRequest;
import com.example.foodchain.orders.dto.OrderLineRequest;
import com.example.foodchain.orders.entity.Order;
import com.example.foodchain.orders.entity.OrderStatus;
import com.example.foodchain.orders.service.OrderService;
import com.example.foodchain.payments.entity.Payment;
import com.example.foodchain.payments.entity.PaymentMethod;
import com.example.foodchain.payments.entity.PaymentStatus;
import com.example.foodchain.payments.service.PaymentService;
import com.example.foodchain.users.entity.Role;
import com.example.foodchain.users.entity.User;
import com.example.foodchain.users.repository.UserRepository;
import java.math.BigDecimal;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.assertj.core.api.Assertions.assertThat;

class PaymentAtomicityTest extends AbstractIntegrationTest {

    @Autowired OrderService orderService;
    @Autowired PaymentService paymentService;
    @Autowired UserRepository userRepository;
    @Autowired ProductRepository productRepository;
    @Autowired OfferRepository offerRepository;
    @Autowired PasswordEncoder passwordEncoder;

    User buyer;
    User seller;
    Offer offer;

    @BeforeEach
    void setup() {
        offerRepository.deleteAll();
        productRepository.deleteAll();
        userRepository.deleteAll();

        seller = userRepository.save(User.create(
                "seller-pay@test.com", passwordEncoder.encode("pass"), Role.AGRICULTEUR));
        seller.setVerified(true);
        seller = userRepository.save(seller);

        buyer = userRepository.save(User.create(
                "buyer-pay@test.com", passwordEncoder.encode("pass"), Role.RESTAURANT));

        Product product = productRepository.save(
                Product.create("Tomates", "Légumes", "kg", BigDecimal.valueOf(500)));
        offer = offerRepository.save(
                Offer.create(seller.getId(), product, BigDecimal.valueOf(100), BigDecimal.valueOf(500), "Abidjan"));
    }

    @Test
    void cashOnDelivery_shouldDecrementStockAndMarkOrderPaid() {
        Order order = orderService.create(buyer.getId(), new CreateOrderRequest(
                List.of(new OrderLineRequest(offer.getId(), BigDecimal.valueOf(10)))));
        orderService.confirm(seller.getId(), order.getId());

        Payment payment = paymentService.initiate(buyer.getId(), order.getId(), PaymentMethod.A_LA_LIVRAISON);

        assertThat(payment.getStatus()).isEqualTo(PaymentStatus.REUSSI);
        assertThat(orderService.getReadable(order.getId(), buyer.getId()).getStatus())
                .isEqualTo(OrderStatus.PAYEE);
        assertThat(offerRepository.findById(offer.getId()).orElseThrow().getQuantity())
                .isEqualByComparingTo(BigDecimal.valueOf(90));
    }

    @Test
    void mobileMoney_onCallbackSuccess_shouldDecrementStockAndMarkOrderPaid() {
        Order order = orderService.create(buyer.getId(), new CreateOrderRequest(
                List.of(new OrderLineRequest(offer.getId(), BigDecimal.valueOf(20)))));
        orderService.confirm(seller.getId(), order.getId());

        Payment initiated = paymentService.initiate(buyer.getId(), order.getId(), PaymentMethod.MOBILE_MONEY);
        assertThat(initiated.getStatus()).isEqualTo(PaymentStatus.INITIE);

        // Stock must NOT be decremented while payment is pending
        assertThat(offerRepository.findById(offer.getId()).orElseThrow().getQuantity())
                .isEqualByComparingTo(BigDecimal.valueOf(100));

        paymentService.handleCallback(initiated.getReference(), PaymentStatus.REUSSI);

        assertThat(orderService.getReadable(order.getId(), buyer.getId()).getStatus())
                .isEqualTo(OrderStatus.PAYEE);
        assertThat(offerRepository.findById(offer.getId()).orElseThrow().getQuantity())
                .isEqualByComparingTo(BigDecimal.valueOf(80));
    }

    @Test
    void failedPayment_shouldNotModifyStockOrOrderStatus() {
        Order order = orderService.create(buyer.getId(), new CreateOrderRequest(
                List.of(new OrderLineRequest(offer.getId(), BigDecimal.valueOf(5)))));
        orderService.confirm(seller.getId(), order.getId());

        Payment initiated = paymentService.initiate(buyer.getId(), order.getId(), PaymentMethod.MOBILE_MONEY);
        paymentService.handleCallback(initiated.getReference(), PaymentStatus.ECHOUE);

        assertThat(offerRepository.findById(offer.getId()).orElseThrow().getQuantity())
                .isEqualByComparingTo(BigDecimal.valueOf(100));
        assertThat(orderService.getReadable(order.getId(), buyer.getId()).getStatus())
                .isEqualTo(OrderStatus.CONFIRMEE);
    }
}
