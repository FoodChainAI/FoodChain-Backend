package com.example.foodchain.orders.controller;

import com.example.foodchain.common.security.SecurityUtils;
import com.example.foodchain.orders.dto.CreateOrderRequest;
import com.example.foodchain.orders.dto.OrderResponse;
import com.example.foodchain.orders.mapper.OrderMapper;
import com.example.foodchain.orders.service.OrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import java.util.UUID;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/orders")
@Tag(name = "Commandes")
public class OrderController {

    private final OrderService orderService;
    private final OrderMapper mapper;

    public OrderController(OrderService orderService, OrderMapper mapper) {
        this.orderService = orderService;
        this.mapper = mapper;
    }

    @Operation(summary = "Passer une commande (acheteur). Née EN_ATTENTE, stock non décrémenté.")
    @PreAuthorize("hasAnyRole('RESTAURANT','GROSSISTE','SUPERMARCHE')")
    @PostMapping
    public ResponseEntity<OrderResponse> create(@Valid @RequestBody CreateOrderRequest request) {
        OrderResponse body = mapper.toResponse(orderService.create(SecurityUtils.currentUserId(), request));
        return ResponseEntity.status(HttpStatus.CREATED).body(body);
    }

    @Operation(summary = "Mes commandes (acheteur)")
    @GetMapping
    public List<OrderResponse> myOrders() {
        return orderService.listForUser(SecurityUtils.currentUserId()).stream().map(mapper::toResponse).toList();
    }

    @Operation(summary = "Commandes reçues (vendeur)")
    @PreAuthorize("hasRole('AGRICULTEUR')")
    @GetMapping("/received")
    public List<OrderResponse> received() {
        return orderService.listReceived(SecurityUtils.currentUserId()).stream().map(mapper::toResponse).toList();
    }

    @Operation(summary = "Détail d'une commande (acheteur ou vendeur concerné)")
    @GetMapping("/{id}")
    public OrderResponse get(@PathVariable UUID id) {
        return mapper.toResponse(orderService.getReadable(id, SecurityUtils.currentUserId()));
    }

    @Operation(summary = "Confirmer une commande (vendeur) : EN_ATTENTE → CONFIRMEE")
    @PreAuthorize("hasRole('AGRICULTEUR')")
    @PostMapping("/{id}/confirm")
    public OrderResponse confirm(@PathVariable UUID id) {
        return mapper.toResponse(orderService.confirm(SecurityUtils.currentUserId(), id));
    }

    @Operation(summary = "Expédier une commande (vendeur) : PAYEE → EXPEDIEE")
    @PreAuthorize("hasRole('AGRICULTEUR')")
    @PostMapping("/{id}/ship")
    public OrderResponse ship(@PathVariable UUID id) {
        return mapper.toResponse(orderService.ship(SecurityUtils.currentUserId(), id));
    }

    @Operation(summary = "Marquer comme livrée : EXPEDIEE → LIVREE")
    @PostMapping("/{id}/deliver")
    public OrderResponse deliver(@PathVariable UUID id) {
        return mapper.toResponse(orderService.deliver(SecurityUtils.currentUserId(), id));
    }

    @Operation(summary = "Annuler une commande : depuis EN_ATTENTE ou CONFIRMEE")
    @PostMapping("/{id}/cancel")
    public OrderResponse cancel(@PathVariable UUID id) {
        return mapper.toResponse(orderService.cancel(SecurityUtils.currentUserId(), id));
    }
}
