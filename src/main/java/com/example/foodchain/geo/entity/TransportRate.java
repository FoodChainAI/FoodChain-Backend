package com.example.foodchain.geo.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

/** Admin-managed transport rate card used by the transport cost estimator. */
@Entity
@Table(name = "transport_rates")
public class TransportRate {

    @Id
    @Column(nullable = false, updatable = false)
    private UUID id;

    @Column(name = "vehicle_class", nullable = false, length = 64)
    private String vehicleClass;

    @Column(name = "cost_per_km_per_ton", nullable = false, precision = 12, scale = 2)
    private BigDecimal costPerKmPerTon;

    @Column(name = "min_cost", nullable = false, precision = 12, scale = 2)
    private BigDecimal minCost;

    @Column(nullable = false)
    private boolean active = true;

    @Column(name = "valid_from", nullable = false)
    private OffsetDateTime validFrom;

    protected TransportRate() {
    }

    public TransportRate(UUID id, String vehicleClass, BigDecimal costPerKmPerTon, BigDecimal minCost,
                         boolean active, OffsetDateTime validFrom) {
        this.id = id;
        this.vehicleClass = vehicleClass;
        this.costPerKmPerTon = costPerKmPerTon;
        this.minCost = minCost;
        this.active = active;
        this.validFrom = validFrom;
    }

    public static TransportRate create(String vehicleClass, BigDecimal costPerKmPerTon, BigDecimal minCost, boolean active) {
        return new TransportRate(UUID.randomUUID(), vehicleClass, costPerKmPerTon, minCost, active, OffsetDateTime.now());
    }

    public UUID getId() {
        return id;
    }

    public String getVehicleClass() {
        return vehicleClass;
    }

    public void setVehicleClass(String vehicleClass) {
        this.vehicleClass = vehicleClass;
    }

    public BigDecimal getCostPerKmPerTon() {
        return costPerKmPerTon;
    }

    public void setCostPerKmPerTon(BigDecimal costPerKmPerTon) {
        this.costPerKmPerTon = costPerKmPerTon;
    }

    public BigDecimal getMinCost() {
        return minCost;
    }

    public void setMinCost(BigDecimal minCost) {
        this.minCost = minCost;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public OffsetDateTime getValidFrom() {
        return validFrom;
    }
}
