package com.example.foodchain.orders.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.UUID;

public record OrderLineRequest(
        @NotNull UUID offerId,
        @NotNull @DecimalMin(value = "0.01", message = "La quantité doit être strictement positive.") BigDecimal quantity
) {
}
