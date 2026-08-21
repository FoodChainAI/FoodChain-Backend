package com.example.foodchain.users.entity;

/**
 * User roles across the marketplace.
 * Sellers publish offers (AGRICULTEUR); buyers place orders (GROSSISTE, RESTAURANT, SUPERMARCHE).
 */
public enum Role {
    AGRICULTEUR,
    GROSSISTE,
    RESTAURANT,
    SUPERMARCHE,
    TRANSPORTEUR,
    ADMIN;

    /** Roles allowed to publish and manage offers. */
    public boolean isSeller() {
        return this == AGRICULTEUR;
    }

    /** Roles allowed to place orders. */
    public boolean isBuyer() {
        return this == GROSSISTE || this == RESTAURANT || this == SUPERMARCHE;
    }
}
