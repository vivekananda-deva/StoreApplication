package com.padmavathi.store.service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.padmavathi.store.entity.Customer;
import com.padmavathi.store.entity.Order;
import com.padmavathi.store.entity.OrderItem;
import com.padmavathi.store.entity.OrderStatus;
import com.padmavathi.store.entity.Product;
import com.padmavathi.store.exception.ResourceNotFoundException;
import com.padmavathi.store.repository.CustomerRepository;
import com.padmavathi.store.repository.OrderRepository;
import com.padmavathi.store.repository.ProductRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final CustomerRepository customerRepository;
    private final ProductRepository productRepository;

    @Transactional
    public Order createOrder(
            String customerName,
            String phone,
            String email,
            String address,
            List<OrderRequestItem> requestedItems) {

        if (requestedItems == null || requestedItems.isEmpty()) {
            throw new IllegalArgumentException(
                    "Order must contain at least one product"
            );
        }

        Customer customer = customerRepository.findByPhone(phone)
                .orElseGet(() -> {
                    Customer newCustomer = new Customer();
                    newCustomer.setName(customerName);
                    newCustomer.setPhone(phone);
                    newCustomer.setEmail(email);
                    newCustomer.setAddress(address);
                    return customerRepository.save(newCustomer);
                });

        customer.setName(customerName);
        customer.setEmail(email);
        customer.setAddress(address);

        Order order = new Order();
        order.setCustomer(customer);
        order.setOrderDate(LocalDateTime.now());
        order.setStatus(OrderStatus.PENDING);

        List<OrderItem> orderItems = new ArrayList<>();
        BigDecimal totalAmount = BigDecimal.ZERO;

        for (OrderRequestItem requestedItem : requestedItems) {

            if (requestedItem.quantity() == null ||
                    requestedItem.quantity() <= 0) {

                throw new IllegalArgumentException(
                        "Quantity must be greater than zero"
                );
            }

            Product product = productRepository
                    .findById(requestedItem.productId())
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "Product not found with id: "
                                    + requestedItem.productId()
                    ));

            BigDecimal price = product.getPrice();

            BigDecimal itemTotal = price.multiply(
                    BigDecimal.valueOf(requestedItem.quantity())
            );

            OrderItem orderItem = new OrderItem();

            orderItem.setOrder(order);
            orderItem.setProduct(product);
            orderItem.setQuantity(requestedItem.quantity());
            orderItem.setPrice(price);

            orderItems.add(orderItem);
            totalAmount = totalAmount.add(itemTotal);
        }

        order.setItems(orderItems);
        order.setTotalAmount(totalAmount);

        return orderRepository.save(order);
    }

    public List<Order> getAllOrders() {
        return orderRepository.findAll();
    }

    public Order getOrderById(Long id) {
        return orderRepository.findById(id)
        		.orElseThrow(() -> new ResourceNotFoundException(
        		        "Order not found with id: " + id
        		));
    }

    public List<Order> getOrdersByCustomer(Long customerId) {
        return orderRepository.findByCustomerId(customerId);
    }

    public List<Order> getOrdersByStatus(OrderStatus status) {
        return orderRepository.findByStatus(status);
    }

    @Transactional
    public Order updateOrderStatus(Long id, OrderStatus status) {
        Order order = getOrderById(id);
        order.setStatus(status);
        return order;
    }

    public void deleteOrder(Long id) {
        Order order = getOrderById(id);
        orderRepository.delete(order);
    }

    public record OrderRequestItem(
            Long productId,
            Integer quantity
    ) {
    }
}