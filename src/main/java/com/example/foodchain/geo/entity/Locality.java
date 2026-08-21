package com.example.foodchain.geo.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.util.UUID;

/**
 * Administrative locality (REGION / DEPARTEMENT / ARRONDISSEMENT).
 *
 * <p>The spatial {@code centroid geography} column is a STORED generated column
 * (derived from latitude/longitude by the database), so this entity maps only the
 * plain lat/lng scalars — no hibernate-spatial dependency needed. Spatial filters
 * (radius search) run as native queries against the GiST-indexed centroid.
 */
@Entity
@Table(name = "localities")
public class Locality {

    @Id
    @Column(nullable = false, updatable = false)
    private UUID id;

    @Column(nullable = false, length = 160)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 16)
    private LocalityLevel level;

    @Column(name = "parent_id")
    private UUID parentId;

    @Column(nullable = false, precision = 9, scale = 6)
    private BigDecimal latitude;

    @Column(nullable = false, precision = 9, scale = 6)
    private BigDecimal longitude;

    @Column(nullable = false)
    private boolean approximate = true;

    protected Locality() {
    }

    public UUID getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public LocalityLevel getLevel() {
        return level;
    }

    public UUID getParentId() {
        return parentId;
    }

    public BigDecimal getLatitude() {
        return latitude;
    }

    public BigDecimal getLongitude() {
        return longitude;
    }

    public boolean isApproximate() {
        return approximate;
    }
}
