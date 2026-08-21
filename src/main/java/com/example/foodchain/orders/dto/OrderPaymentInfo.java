package com.example.foodchain.orders.dto;

import com.example.foodchain.catalog.dto.StockDecrement;
import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

/**
 * Snapshot the payments module needs to settle an order: the amount to charge
 * and the per-offer stock decrements to apply on a successful payment.
 */
public record OrderPaymentInfo(
        UUID orderId,
        UUID buyerId,
        BigDecimal amount,
        List<StockDecrement> decrements
) {
}
