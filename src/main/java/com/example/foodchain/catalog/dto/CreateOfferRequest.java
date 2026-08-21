package com.example.foodchain.catalog.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.UUID;

public record CreateOfferRequest(
        @NotNull UUID productId,
        @NotNull @DecimalMin(value = "0.0", message = "La quantité doit être positive.") BigDecimal quantity,
        @NotNull @DecimalMin(value = "0.0", message = "Le prix doit être positif.") BigDecimal price,
        @NotBlank String location
) {
}
