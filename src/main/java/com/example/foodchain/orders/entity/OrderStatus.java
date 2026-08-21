package com.example.foodchain.orders.entity;

import java.util.Map;
import java.util.Set;

/**
 * Order lifecycle state machine.
 * <pre>
 * EN_ATTENTE → CONFIRMEE → PAYEE → EXPEDIEE → LIVREE
 * EN_ATTENTE, CONFIRMEE → ANNULEE
 * </pre>
 * Only the transitions declared in {@link #ALLOWED} are permitted.
 */
public enum OrderStatus {
    EN_ATTENTE,
    CONFIRMEE,
    PAYEE,
    EXPEDIEE,
    LIVREE,
    ANNULEE;

    private static final Map<OrderStatus, Set<OrderStatus>> ALLOWED = Map.of(
            EN_ATTENTE, Set.of(CONFIRMEE, ANNULEE),
            CONFIRMEE, Set.of(PAYEE, ANNULEE),
            PAYEE, Set.of(EXPEDIEE),
            EXPEDIEE, Set.of(LIVREE),
            LIVREE, Set.of(),
            ANNULEE, Set.of());

    public boolean canTransitionTo(OrderStatus target) {
        return ALLOWED.getOrDefault(this, Set.of()).contains(target);
    }
}
