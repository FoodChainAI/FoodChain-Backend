package com.example.foodchain.orders.mapper;

import com.example.foodchain.orders.dto.OrderLineResponse;
import com.example.foodchain.orders.dto.OrderResponse;
import com.example.foodchain.orders.entity.Order;
import com.example.foodchain.orders.entity.OrderLine;
import org.springframework.stereotype.Component;

@Component
public class OrderMapper {

    public OrderResponse toResponse(Order order) {
        return new OrderResponse(
                order.getId(),
                order.getBuyerId(),
                order.getStatus(),
                order.getTotal(),
                order.getCreatedAt(),
                order.getLines().stream().map(this::toLineResponse).toList());
    }

    private OrderLineResponse toLineResponse(OrderLine line) {
        return new OrderLineResponse(
                line.getId(),
                line.getOfferId(),
                line.getQuantity(),
                line.getUnitPrice(),
                line.lineTotal());
    }
}
