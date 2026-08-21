package com.example.foodchain.payments.repository;

import com.example.foodchain.payments.entity.Payment;
import com.example.foodchain.payments.entity.PaymentStatus;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PaymentRepository extends JpaRepository<Payment, UUID> {
    Optional<Payment> findByReference(String reference);

    boolean existsByOrderIdAndStatus(UUID orderId, PaymentStatus status);
}
