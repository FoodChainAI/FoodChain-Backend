package com.example.foodchain.orders.repository;

import com.example.foodchain.orders.entity.Order;
import java.util.Collection;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderRepository extends JpaRepository<Order, UUID> {
    List<Order> findByBuyerIdOrderByCreatedAtDesc(UUID buyerId);

    List<Order> findDistinctByLines_OfferIdInOrderByCreatedAtDesc(Collection<UUID> offerIds);
}
