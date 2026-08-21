package com.example.foodchain.catalog.dto;

import java.math.BigDecimal;
import java.util.UUID;

/** A request to decrement one offer's stock by {@code quantity}. */
public record StockDecrement(UUID offerId, BigDecimal quantity) {
}
