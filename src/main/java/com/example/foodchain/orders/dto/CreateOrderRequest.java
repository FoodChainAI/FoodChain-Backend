package com.example.foodchain.orders.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import java.util.List;

public record CreateOrderRequest(
        @NotEmpty(message = "La commande doit contenir au moins une ligne.")
        @Valid List<OrderLineRequest> lines
) {
}
