package com.example.foodchain.catalog.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

/**
 * Append-only price observations per product. Foundation for the Phase 3
 * price-intelligence features; written whenever an offer is created/updated.
 */
@Entity
@Table(name = "price_history")
public class PriceHistory {

    @Id
    @Column(nullable = false, updatable = false)
    private UUID id;

    @Column(name = "product_id", nullable = false)
    private UUID productId;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal price;

    @Column(nullable = false)
    private OffsetDateTime ts;

    protected PriceHistory() {
    }

    public PriceHistory(UUID id, UUID productId, BigDecimal price, OffsetDateTime ts) {
        this.id = id;
        this.productId = productId;
        this.price = price;
        this.ts = ts;
    }

    public static PriceHistory record(UUID productId, BigDecimal price) {
        return new PriceHistory(UUID.randomUUID(), productId, price, OffsetDateTime.now());
    }

    public UUID getId() {
        return id;
    }

    public UUID getProductId() {
        return productId;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public OffsetDateTime getTs() {
        return ts;
    }
}
