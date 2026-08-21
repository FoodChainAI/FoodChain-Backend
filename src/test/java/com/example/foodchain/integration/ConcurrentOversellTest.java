package com.example.foodchain.integration;

import com.example.foodchain.AbstractIntegrationTest;
import com.example.foodchain.catalog.entity.Offer;
import com.example.foodchain.catalog.entity.Product;
import com.example.foodchain.catalog.repository.OfferRepository;
import com.example.foodchain.catalog.repository.ProductRepository;
import com.example.foodchain.orders.dto.CreateOrderRequest;
import com.example.foodchain.orders.dto.OrderLineRequest;
import com.example.foodchain.orders.entity.Order;
import com.example.foodchain.orders.service.OrderService;
import com.example.foodchain.payments.entity.PaymentMethod;
import com.example.foodchain.payments.repository.PaymentRepository;
import com.example.foodchain.payments.service.PaymentService;
import com.example.foodchain.users.entity.Role;
import com.example.foodchain.users.entity.User;
import com.example.foodchain.users.repository.UserRepository;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * 5 acheteurs tentent simultanément de payer 30 unités chacun.
 * Le stock initial est 100 — au plus 3 paiements peuvent aboutir.
 * Vérifie que le stock ne descend jamais en dessous de 0 (survente impossible).
 */
class ConcurrentOversellTest extends AbstractIntegrationTest {

    @Autowired OrderService orderService;
    @Autowired PaymentService paymentService;
    @Autowired UserRepository userRepository;
    @Autowired ProductRepository productRepository;
    @Autowired OfferRepository offerRepository;
    @Autowired PaymentRepository paymentRepository;
    @Autowired PasswordEncoder passwordEncoder;

    User seller;
    Offer offer;
    final List<User> buyers = new ArrayList<>();

    @BeforeEach
    void setup() {
        paymentRepository.deleteAll();
        offerRepository.deleteAll();
        productRepository.deleteAll();
        userRepository.deleteAll();
        buyers.clear();

        seller = userRepository.save(User.create(
                "seller-concurrent@test.com", passwordEncoder.encode("pass"), Role.AGRICULTEUR));
        seller.setVerified(true);
        seller = userRepository.save(seller);

        for (int i = 0; i < 5; i++) {
            buyers.add(userRepository.save(User.create(
                    "buyer-concurrent-" + i + "@test.com", passwordEncoder.encode("pass"), Role.RESTAURANT)));
        }

        Product product = productRepository.save(
                Product.create("Maïs concurrence", "Céréales", "kg", BigDecimal.valueOf(350)));
        offer = offerRepository.save(
                Offer.create(seller.getId(), product, BigDecimal.valueOf(100), BigDecimal.valueOf(300), "Abidjan"));
    }

    @Test
    void shouldPreventOversellUnderConcurrency() throws InterruptedException {
        BigDecimal qty = BigDecimal.valueOf(30);
        List<Order> orders = new ArrayList<>();
        for (User buyer : buyers) {
            Order o = orderService.create(buyer.getId(), new CreateOrderRequest(
                    List.of(new OrderLineRequest(offer.getId(), qty))));
            orderService.confirm(seller.getId(), o.getId());
            orders.add(o);
        }

        CountDownLatch startLatch = new CountDownLatch(1);
        ExecutorService executor = Executors.newFixedThreadPool(5);
        List<Future<Boolean>> futures = new ArrayList<>();

        for (int i = 0; i < 5; i++) {
            User buyer = buyers.get(i);
            Order order = orders.get(i);
            futures.add(executor.submit(() -> {
                startLatch.await();
                try {
                    paymentService.initiate(buyer.getId(), order.getId(), PaymentMethod.A_LA_LIVRAISON);
                    return true;
                } catch (Exception e) {
                    return false;
                }
            }));
        }

        startLatch.countDown();
        executor.shutdown();

        long successes = futures.stream().filter(f -> {
            try { return f.get(); } catch (Exception e) { return false; }
        }).count();

        Offer finalOffer = offerRepository.findById(offer.getId()).orElseThrow();

        // Stock doit rester >= 0 (pas de survente)
        assertThat(finalOffer.getQuantity()).isGreaterThanOrEqualTo(BigDecimal.ZERO);
        // Au plus 3 lots de 30 dans 100 unités
        assertThat(successes).isLessThanOrEqualTo(3);
        // Cohérence : stock restant = initial - (succès * 30)
        assertThat(finalOffer.getQuantity())
                .isEqualByComparingTo(BigDecimal.valueOf(100).subtract(qty.multiply(BigDecimal.valueOf(successes))));
    }
}
