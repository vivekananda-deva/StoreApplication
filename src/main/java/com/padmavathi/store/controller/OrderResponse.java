package com.padmavathi.store.controller;

import com.padmavathi.store.entity.OrderStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public record OrderResponse(
        Long id,
        CustomerResponse customer,
        LocalDateTime orderDate,
        OrderStatus status,
        BigDecimal totalAmount,
        List<OrderItemResponse> items
) {

    public record CustomerResponse(
            Long id,
            String name,
            String phone,
            String email,
            String address
    ) {
    }

    public record OrderItemResponse(
            Long id,
            Long productId,
            String productName,
            Integer quantity,
            BigDecimal price,
            BigDecimal itemTotal
    ) {
    }
}