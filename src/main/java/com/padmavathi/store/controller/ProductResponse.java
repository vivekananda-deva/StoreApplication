package com.padmavathi.store.controller;

import java.math.BigDecimal;

public record ProductResponse(
        Long id,
        String name,
        String description,
        BigDecimal price,
        String imageUrl,
        String size,
        CategoryResponse category
) {

    public record CategoryResponse(
            Long id,
            String name
    ) {
    }
}