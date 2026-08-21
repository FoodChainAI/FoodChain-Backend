package com.example.foodchain.orders.service;

import com.example.foodchain.catalog.dto.OfferSnapshot;
import com.example.foodchain.catalog.dto.StockDecrement;
import com.example.foodchain.catalog.service.OfferService;
import com.example.foodchain.common.error.ConflictException;
import com.example.foodchain.common.error.ForbiddenException;
import com.example.foodchain.common.error.NotFoundException;
import com.example.foodchain.orders.dto.CreateOrderRequest;
import com.example.foodchain.orders.dto.OrderLineRequest;
import com.example.foodchain.orders.dto.OrderPaymentInfo;
import com.example.foodchain.orders.entity.Order;
import com.example.foodchain.orders.entity.OrderLine;
import com.example.foodchain.orders.entity.OrderStatus;
import com.example.foodchain.orders.repository.OrderRepository;
import com.example.foodchain.users.entity.User;
import com.example.foodchain.users.service.UserService;
import java.util.List;
import java.util.UUID;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class OrderService {

    private final OrderRepository orderRepository;
    private final OfferService offerService;
    private final UserService userService;

    public OrderService(OrderRepository orderRepository, OfferService offerService, UserService userService) {
        this.orderRepository = orderRepository;
        this.offerService = offerService;
        this.userService = userService;
    }

    /**
     * Creates an order in EN_ATTENTE. Availability is verified per offer and the
     * unit price is snapshotted, but stock is NOT decremented at creation time
     * (that happens only on a successful payment).
     */
    @Transactional
    public Order create(UUID buyerId, CreateOrderRequest request) {
        User buyer = userService.getById(buyerId);
        if (!buyer.getRole().isBuyer()) {
            throw new ForbiddenException("Seuls les acheteurs (restaurant, grossiste, supermarché) peuvent commander.");
        }
        Order order = Order.create(buyerId);
        for (OrderLineRequest lineReq : request.lines()) {
            OfferSnapshot snapshot = offerService.getSnapshot(lineReq.offerId());
            if (!snapshot.canSatisfy(lineReq.quantity())) {
                throw new ConflictException("OFFER_UNAVAILABLE",
                        "L'offre " + snapshot.offerId() + " n'est pas disponible dans la quantité demandée.",
                        java.util.Map.of(
                                "offerId", snapshot.offerId().toString(),
                                "requested", lineReq.quantity(),
                                "available", snapshot.availableQuantity()));
            }
            order.addLine(OrderLine.create(order, snapshot.offerId(), lineReq.quantity(), snapshot.unitPrice()));
        }
        order.recomputeTotal();
        return orderRepository.save(order);
    }

    @Transactional(readOnly = true)
    public List<Order> listForUser(UUID userId) {
        return orderRepository.findByBuyerIdOrderByCreatedAtDesc(userId);
    }

    /** Orders that include at least one line for one of the seller's offers. */
    @Transactional(readOnly = true)
    public List<Order> listReceived(UUID sellerId) {
        List<UUID> offerIds = offerService.findOfferIdsBySeller(sellerId);
        if (offerIds.isEmpty()) {
            return List.of();
        }
        return orderRepository.findDistinctByLines_OfferIdInOrderByCreatedAtDesc(offerIds);
    }

    @Transactional(readOnly = true)
    public Order getReadable(UUID orderId, UUID requesterId) {
        Order order = load(orderId);
        if (!order.getBuyerId().equals(requesterId) && !sellerOwns(order, requesterId)) {
            throw new ForbiddenException("Vous n'avez pas accès à cette commande.");
        }
        return order;
    }

    @Transactional
    public Order confirm(UUID sellerId, UUID orderId) {
        Order order = load(orderId);
        requireSellerOwnership(order, sellerId);
        order.transitionTo(OrderStatus.CONFIRMEE);
        return order;
    }

    @Transactional
    public Order ship(UUID sellerId, UUID orderId) {
        Order order = load(orderId);
        requireSellerOwnership(order, sellerId);
        order.transitionTo(OrderStatus.EXPEDIEE);
        return order;
    }

    @Transactional
    public Order deliver(UUID actorId, UUID orderId) {
        Order order = load(orderId);
        if (!order.getBuyerId().equals(actorId) && !sellerOwns(order, actorId)) {
            throw new ForbiddenException("Seuls l'acheteur ou le vendeur peuvent marquer la livraison.");
        }
        order.transitionTo(OrderStatus.LIVREE);
        return order;
    }

    @Transactional
    public Order cancel(UUID actorId, UUID orderId) {
        Order order = load(orderId);
        if (!order.getBuyerId().equals(actorId) && !sellerOwns(order, actorId)) {
            throw new ForbiddenException("Seuls l'acheteur ou le vendeur peuvent annuler la commande.");
        }
        order.transitionTo(OrderStatus.ANNULEE);
        return order;
    }

    // ---- Cross-module API used by the payments module ----

    /**
     * Validates that {@code buyerId} owns a CONFIRMEE order and returns the
     * amount to charge plus the stock decrements to apply on success.
     */
    @Transactional(readOnly = true)
    public OrderPaymentInfo getPayableOrder(UUID orderId, UUID buyerId) {
        Order order = load(orderId);
        if (!order.getBuyerId().equals(buyerId)) {
            throw new ForbiddenException("Cette commande ne vous appartient pas.");
        }
        if (order.getStatus() != OrderStatus.CONFIRMEE) {
            throw new ConflictException("ORDER_NOT_PAYABLE",
                    "La commande doit être CONFIRMEE pour être payée (statut actuel: " + order.getStatus() + ").",
                    java.util.Map.of("status", order.getStatus().name()));
        }
        List<StockDecrement> decrements = order.getLines().stream()
                .map(l -> new StockDecrement(l.getOfferId(), l.getQuantity()))
                .toList();
        return new OrderPaymentInfo(order.getId(), order.getBuyerId(), order.getTotal(), decrements);
    }

    /**
     * Internal settlement lookup (no ownership check): used by the payment
     * callback where only the payment reference is known. Requires CONFIRMEE.
     */
    @Transactional(readOnly = true)
    public OrderPaymentInfo getSettlementInfo(UUID orderId) {
        Order order = load(orderId);
        if (order.getStatus() != OrderStatus.CONFIRMEE) {
            throw new ConflictException("ORDER_NOT_PAYABLE",
                    "La commande doit être CONFIRMEE pour être réglée (statut actuel: " + order.getStatus() + ").",
                    java.util.Map.of("status", order.getStatus().name()));
        }
        List<StockDecrement> decrements = order.getLines().stream()
                .map(l -> new StockDecrement(l.getOfferId(), l.getQuantity()))
                .toList();
        return new OrderPaymentInfo(order.getId(), order.getBuyerId(), order.getTotal(), decrements);
    }

    /** Transitions CONFIRMEE → PAYEE. Called within the payment settlement transaction. */
    @Transactional
    public void markPaid(UUID orderId) {
        load(orderId).transitionTo(OrderStatus.PAYEE);
    }

    // ---- Cross-module API used by the trust (reviews) module ----

    /** Ensures the order is LIVREE and owned by {@code buyerId}; otherwise throws. */
    @Transactional(readOnly = true)
    public void requireDeliveredOrder(UUID orderId, UUID buyerId) {
        Order order = load(orderId);
        if (!order.getBuyerId().equals(buyerId)) {
            throw new ForbiddenException("Vous ne pouvez noter que vos propres commandes.");
        }
        if (order.getStatus() != OrderStatus.LIVREE) {
            throw new ConflictException("ORDER_NOT_DELIVERED",
                    "Une note n'est possible que sur une commande LIVREE.",
                    java.util.Map.of("status", order.getStatus().name()));
        }
    }

    // ---- helpers ----

    private Order load(UUID orderId) {
        return orderRepository.findById(orderId)
                .orElseThrow(() -> new NotFoundException("Commande introuvable."));
    }

    private void requireSellerOwnership(Order order, UUID sellerId) {
        if (!sellerOwns(order, sellerId)) {
            throw new ForbiddenException("Vous n'êtes pas le vendeur de cette commande.");
        }
    }

    private boolean sellerOwns(Order order, UUID sellerId) {
        return order.getLines().stream()
                .allMatch(l -> offerService.getSnapshot(l.getOfferId()).sellerId().equals(sellerId))
                && !order.getLines().isEmpty();
    }
}
