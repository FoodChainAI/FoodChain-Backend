package com.example.foodchain.payments.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "payments")
public class Payment {

    @Id
    @Column(nullable = false, updatable = false)
    private UUID id;

    @Column(name = "order_id", nullable = false)
    private UUID orderId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 32)
    private PaymentMethod method;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 32)
    private PaymentStatus status;

    @Column(nullable = false, precision = 14, scale = 2)
    private BigDecimal amount;

    @Column(nullable = false, unique = true, length = 128)
    private String reference;

    @Column(name = "created_at", nullable = false, updatable = false)
    private OffsetDateTime createdAt;

    protected Payment() {
    }

    private Payment(UUID id, UUID orderId, PaymentMethod method, PaymentStatus status,
                    BigDecimal amount, String reference, OffsetDateTime createdAt) {
        this.id = id;
        this.orderId = orderId;
        this.method = method;
        this.status = status;
        this.amount = amount;
        this.reference = reference;
        this.createdAt = createdAt;
    }

    public static Payment initiate(UUID orderId, PaymentMethod method, BigDecimal amount, String reference) {
        return new Payment(UUID.randomUUID(), orderId, method, PaymentStatus.INITIE, amount, reference,
                OffsetDateTime.now());
    }

    public void markSucceeded() {
        this.status = PaymentStatus.REUSSI;
    }

    public void markFailed() {
        this.status = PaymentStatus.ECHOUE;
    }

    public boolean isPending() {
        return status == PaymentStatus.INITIE;
    }

    public UUID getId() {
        return id;
    }

    public UUID getOrderId() {
        return orderId;
    }

    public PaymentMethod getMethod() {
        return method;
    }

    public PaymentStatus getStatus() {
        return status;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public String getReference() {
        return reference;
    }

    public OffsetDateTime getCreatedAt() {
        return createdAt;
    }
}
