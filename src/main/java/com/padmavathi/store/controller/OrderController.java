package com.padmavathi.store.controller;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.padmavathi.store.entity.Order;
import com.padmavathi.store.entity.OrderItem;
import com.padmavathi.store.entity.OrderStatus;
import com.padmavathi.store.service.OrderService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @PostMapping
    public ResponseEntity<OrderResponse> createOrder(
            @Valid @RequestBody OrderRequest request) {

        List<OrderService.OrderRequestItem> items =
                request.items()
                        .stream()
                        .map(item -> new OrderService.OrderRequestItem(
                                item.productId(),
                                item.quantity()
                        ))
                        .toList();

        Order order = orderService.createOrder(
                request.customerName(),
                request.phone(),
                request.email(),
                request.address(),
                items
        );

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(toResponse(order));
    }

    @GetMapping
    public ResponseEntity<List<OrderResponse>> getAllOrders() {

        List<OrderResponse> orders = orderService.getAllOrders()
                .stream()
                .map(this::toResponse)
                .toList();

        return ResponseEntity.ok(orders);
    }

    @GetMapping("/{id}")
    public ResponseEntity<OrderResponse> getOrderById(
            @PathVariable Long id) {

        return ResponseEntity.ok(
                toResponse(orderService.getOrderById(id))
        );
    }

    @GetMapping("/customer/{customerId}")
    public ResponseEntity<List<OrderResponse>> getOrdersByCustomer(
            @PathVariable Long customerId) {

        List<OrderResponse> orders =
                orderService.getOrdersByCustomer(customerId)
                        .stream()
                        .map(this::toResponse)
                        .toList();

        return ResponseEntity.ok(orders);
    }

    @GetMapping("/status/{status}")
    public ResponseEntity<List<OrderResponse>> getOrdersByStatus(
            @PathVariable OrderStatus status) {

        List<OrderResponse> orders =
                orderService.getOrdersByStatus(status)
                        .stream()
                        .map(this::toResponse)
                        .toList();

        return ResponseEntity.ok(orders);
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<OrderResponse> updateOrderStatus(
            @PathVariable Long id,
            @RequestParam OrderStatus status) {

        return ResponseEntity.ok(
                toResponse(
                        orderService.updateOrderStatus(id, status)
                )
        );
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteOrder(
            @PathVariable Long id) {

        orderService.deleteOrder(id);
        return ResponseEntity.noContent().build();
    }

    private OrderResponse toResponse(Order order) {

        OrderResponse.CustomerResponse customer =
                new OrderResponse.CustomerResponse(
                        order.getCustomer().getId(),
                        order.getCustomer().getName(),
                        order.getCustomer().getPhone(),
                        order.getCustomer().getEmail(),
                        order.getCustomer().getAddress()
                );

        List<OrderResponse.OrderItemResponse> items =
                order.getItems()
                        .stream()
                        .map(this::toItemResponse)
                        .toList();

        return new OrderResponse(
                order.getId(),
                customer,
                order.getOrderDate(),
                order.getStatus(),
                order.getTotalAmount(),
                items
        );
    }

    private OrderResponse.OrderItemResponse toItemResponse(
            OrderItem item) {

    	BigDecimal itemTotal =
    	        item.getPrice().multiply(
    	                BigDecimal.valueOf(item.getQuantity())
    	        );
        return new OrderResponse.OrderItemResponse(
                item.getId(),
                item.getProduct().getId(),
                item.getProduct().getName()
                        + (item.getProduct().getSize() == null || item.getProduct().getSize().isBlank()
                                ? "" : " " + item.getProduct().getSize()),
                item.getQuantity(),
                item.getPrice(),
                itemTotal
        );
    }
}