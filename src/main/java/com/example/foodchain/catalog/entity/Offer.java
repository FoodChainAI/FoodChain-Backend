package com.example.foodchain.catalog.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.Version;
import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "offers")
public class Offer {

    @Id
    @Column(nullable = false, updatable = false)
    private UUID id;

    @Column(name = "seller_id", nullable = false)
    private UUID sellerId;

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal quantity;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal price;

    @Column(nullable = false)
    private boolean available;

    @Column(nullable = false)
    private String location;

    @Version
    @Column(nullable = false)
    private long version;

    @Column(name = "created_at", nullable = false, updatable = false)
    private OffsetDateTime createdAt;

    protected Offer() {
    }

    public Offer(UUID id, UUID sellerId, Product product, BigDecimal quantity, BigDecimal price,
                 boolean available, String location, OffsetDateTime createdAt) {
        this.id = id;
        this.sellerId = sellerId;
        this.product = product;
        this.quantity = quantity;
        this.price = price;
        this.available = available;
        this.location = location;
        this.createdAt = createdAt;
    }

    public static Offer create(UUID sellerId, Product product, BigDecimal quantity, BigDecimal price, String location) {
        boolean available = quantity.signum() > 0;
        return new Offer(UUID.randomUUID(), sellerId, product, quantity, price, available, location, OffsetDateTime.now());
    }

    /** Decrements stock by {@code amount}; flips availability off when depleted. */
    public void decreaseStock(BigDecimal amount) {
        this.quantity = this.quantity.subtract(amount);
        if (this.quantity.signum() <= 0) {
            this.quantity = BigDecimal.ZERO;
            this.available = false;
        }
    }

    public boolean hasStock(BigDecimal amount) {
        return available && quantity.compareTo(amount) >= 0;
    }

    public UUID getId() {
        return id;
    }

    public UUID getSellerId() {
        return sellerId;
    }

    public Product getProduct() {
        return product;
    }

    public BigDecimal getQuantity() {
        return quantity;
    }

    public void setQuantity(BigDecimal quantity) {
        this.quantity = quantity;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public boolean isAvailable() {
        return available;
    }

    public void setAvailable(boolean available) {
        this.available = available;
    }

    public String getLocation() {
        return location;
    }

    public long getVersion() {
        return version;
    }

    public OffsetDateTime getCreatedAt() {
        return createdAt;
    }
}
