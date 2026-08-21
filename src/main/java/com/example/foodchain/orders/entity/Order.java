package com.example.foodchain.orders.entity;

import com.example.foodchain.common.error.ConflictException;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Entity
@Table(name = "orders")
public class Order {

    @Id
    @Column(nullable = false, updatable = false)
    private UUID id;

    @Column(name = "buyer_id", nullable = false)
    private UUID buyerId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 32)
    private OrderStatus status;

    @Column(nullable = false, precision = 14, scale = 2)
    private BigDecimal total;

    @Column(name = "created_at", nullable = false, updatable = false)
    private OffsetDateTime createdAt;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OrderLine> lines = new ArrayList<>();

    protected Order() {
    }

    private Order(UUID id, UUID buyerId, OrderStatus status, BigDecimal total, OffsetDateTime createdAt) {
        this.id = id;
        this.buyerId = buyerId;
        this.status = status;
        this.total = total;
        this.createdAt = createdAt;
    }

    public static Order create(UUID buyerId) {
        return new Order(UUID.randomUUID(), buyerId, OrderStatus.EN_ATTENTE, BigDecimal.ZERO, OffsetDateTime.now());
    }

    public void addLine(OrderLine line) {
        lines.add(line);
    }

    public void recomputeTotal() {
        this.total = lines.stream().map(OrderLine::lineTotal).reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    /**
     * Applies a lifecycle transition, rejecting invalid ones with an explicit
     * {@link ConflictException} (code INVALID_TRANSITION).
     */
    public void transitionTo(OrderStatus target) {
        if (!status.canTransitionTo(target)) {
            throw new ConflictException("INVALID_TRANSITION",
                    "Transition de commande invalide: " + status + " → " + target + ".",
                    Map.of("from", status.name(), "to", target.name()));
        }
        this.status = target;
    }

    public UUID getId() {
        return id;
    }

    public UUID getBuyerId() {
        return buyerId;
    }

    public OrderStatus getStatus() {
        return status;
    }

    public BigDecimal getTotal() {
        return total;
    }

    public OffsetDateTime getCreatedAt() {
        return createdAt;
    }

    public List<OrderLine> getLines() {
        return lines;
    }
}
