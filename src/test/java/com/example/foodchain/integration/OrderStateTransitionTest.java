package com.example.foodchain.integration;

import com.example.foodchain.AbstractIntegrationTest;
import com.example.foodchain.catalog.entity.Offer;
import com.example.foodchain.catalog.entity.Product;
import com.example.foodchain.catalog.repository.OfferRepository;
import com.example.foodchain.catalog.repository.ProductRepository;
import com.example.foodchain.common.error.ConflictException;
import com.example.foodchain.orders.dto.CreateOrderRequest;
import com.example.foodchain.orders.dto.OrderLineRequest;
import com.example.foodchain.orders.entity.Order;
import com.example.foodchain.orders.entity.OrderStatus;
import com.example.foodchain.orders.service.OrderService;
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
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class OrderStateTransitionTest extends AbstractIntegrationTest {

    @Autowired OrderService orderService;
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
                "seller-trans@test.com", passwordEncoder.encode("pass"), Role.AGRICULTEUR));
        seller.setVerified(true);
        seller = userRepository.save(seller);

        buyer = userRepository.save(User.create(
                "buyer-trans@test.com", passwordEncoder.encode("pass"), Role.RESTAURANT));

        Product product = productRepository.save(
                Product.create("Maïs", "Céréales", "kg", BigDecimal.valueOf(350)));
        offer = offerRepository.save(
                Offer.create(seller.getId(), product, BigDecimal.valueOf(100), BigDecimal.valueOf(300), "Abidjan"));
    }

    @Test
    void shouldTransitionFromEnAttenteToConfirmee() {
        Order order = orderService.create(buyer.getId(), new CreateOrderRequest(
                List.of(new OrderLineRequest(offer.getId(), BigDecimal.TEN))));
        assertThat(order.getStatus()).isEqualTo(OrderStatus.EN_ATTENTE);

        Order confirmed = orderService.confirm(seller.getId(), order.getId());
        assertThat(confirmed.getStatus()).isEqualTo(OrderStatus.CONFIRMEE);
    }

    @Test
    void shouldRejectInvalidTransitionFromEnAttenteToPayee() {
        Order order = orderService.create(buyer.getId(), new CreateOrderRequest(
                List.of(new OrderLineRequest(offer.getId(), BigDecimal.TEN))));

        assertThatThrownBy(() -> orderService.markPaid(order.getId()))
                .isInstanceOf(ConflictException.class)
                .hasMessageContaining("CONFIRMEE");
    }

    @Test
    void shouldAllowCancellationFromEnAttente() {
        Order order = orderService.create(buyer.getId(), new CreateOrderRequest(
                List.of(new OrderLineRequest(offer.getId(), BigDecimal.TEN))));

        Order cancelled = orderService.cancel(buyer.getId(), order.getId());
        assertThat(cancelled.getStatus()).isEqualTo(OrderStatus.ANNULEE);
    }

    @Test
    void shouldAllowCancellationFromConfirmee() {
        Order order = orderService.create(buyer.getId(), new CreateOrderRequest(
                List.of(new OrderLineRequest(offer.getId(), BigDecimal.TEN))));
        orderService.confirm(seller.getId(), order.getId());

        Order cancelled = orderService.cancel(buyer.getId(), order.getId());
        assertThat(cancelled.getStatus()).isEqualTo(OrderStatus.ANNULEE);
    }

    @Test
    void shouldRejectTransitionFromAnnuleeToAnyState() {
        Order order = orderService.create(buyer.getId(), new CreateOrderRequest(
                List.of(new OrderLineRequest(offer.getId(), BigDecimal.TEN))));
        orderService.cancel(buyer.getId(), order.getId());

        assertThatThrownBy(() -> orderService.confirm(seller.getId(), order.getId()))
                .isInstanceOf(ConflictException.class)
                .hasMessageContaining("INVALID_TRANSITION");
    }

    @Test
    void shouldRejectOrderWhenOfferUnavailable() {
        offer.setQuantity(BigDecimal.ZERO);
        offer.setAvailable(false);
        offerRepository.save(offer);

        assertThatThrownBy(() -> orderService.create(buyer.getId(), new CreateOrderRequest(
                List.of(new OrderLineRequest(offer.getId(), BigDecimal.ONE)))))
                .isInstanceOf(ConflictException.class)
                .hasMessageContaining("OFFER_UNAVAILABLE");
    }
}
