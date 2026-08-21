package com.example.foodchain.catalog.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;

/** Sets the absolute stock quantity of an offer. */
public record UpdateStockRequest(
        @NotNull @DecimalMin(value = "0.0", message = "La quantité doit être positive ou nulle.") BigDecimal quantity
) {
}
