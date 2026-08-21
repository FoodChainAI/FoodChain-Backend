package com.example.foodchain.orders.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Table(name = "order_lines")
public class OrderLine {

    @Id
    @Column(nullable = false, updatable = false)
    private UUID id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "order_id", nullable = false)
    private Order order;

    @Column(name = "offer_id", nullable = false)
    private UUID offerId;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal quantity;

    @Column(name = "unit_price", nullable = false, precision = 12, scale = 2)
    private BigDecimal unitPrice;

    protected OrderLine() {
    }

    public OrderLine(UUID id, Order order, UUID offerId, BigDecimal quantity, BigDecimal unitPrice) {
        this.id = id;
        this.order = order;
        this.offerId = offerId;
        this.quantity = quantity;
        this.unitPrice = unitPrice;
    }

    public static OrderLine create(Order order, UUID offerId, BigDecimal quantity, BigDecimal unitPrice) {
        return new OrderLine(UUID.randomUUID(), order, offerId, quantity, unitPrice);
    }

    public BigDecimal lineTotal() {
        return unitPrice.multiply(quantity);
    }

    public UUID getId() {
        return id;
    }

    public Order getOrder() {
        return order;
    }

    public UUID getOfferId() {
        return offerId;
    }

    public BigDecimal getQuantity() {
        return quantity;
    }

    public BigDecimal getUnitPrice() {
        return unitPrice;
    }
}
