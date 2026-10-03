package com.padmavathi.store.dto;

import java.math.BigDecimal;

public class OrderItemResponse {
    private String productName;
    private int quantity;
    private BigDecimal priceAtOrder;
    private String unit;

    public OrderItemResponse(String productName, int quantity, BigDecimal priceAtOrder, String unit) {
        this.productName = productName;
        this.quantity = quantity;
        this.priceAtOrder = priceAtOrder;
        this.unit = unit;
    }

    public String getProductName() { return productName; }
    public int getQuantity() { return quantity; }
    public BigDecimal getPriceAtOrder() { return priceAtOrder; }
    public String getUnit() { return unit; }
}
