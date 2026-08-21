package com.example.foodchain.catalog.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Table(name = "products")
public class Product {

    @Id
    @Column(nullable = false, updatable = false)
    private UUID id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String category;

    @Column(nullable = false, length = 32)
    private String unit;

    @Column(name = "base_price", nullable = false, precision = 12, scale = 2)
    private BigDecimal basePrice;

    protected Product() {
    }

    public Product(UUID id, String name, String category, String unit, BigDecimal basePrice) {
        this.id = id;
        this.name = name;
        this.category = category;
        this.unit = unit;
        this.basePrice = basePrice;
    }

    public static Product create(String name, String category, String unit, BigDecimal basePrice) {
        return new Product(UUID.randomUUID(), name, category, unit, basePrice);
    }

    public UUID getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getCategory() {
        return category;
    }

    public String getUnit() {
        return unit;
    }

    public BigDecimal getBasePrice() {
        return basePrice;
    }
}
