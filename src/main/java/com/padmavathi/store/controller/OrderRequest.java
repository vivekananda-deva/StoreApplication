package com.padmavathi.store.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import java.util.List;

public record OrderRequest(

        @NotBlank(message = "Customer name is required")
        @Size(max = 100, message = "Customer name must not exceed 100 characters")
        String customerName,

        @NotBlank(message = "Phone number is required")
        @Pattern(
                regexp = "^[0-9]{10}$",
                message = "Phone number must contain exactly 10 digits"
        )
        String phone,

        @Email(message = "Enter a valid email address")
        @Size(max = 150, message = "Email must not exceed 150 characters")
        String email,

        @NotBlank(message = "Address is required")
        @Size(max = 500, message = "Address must not exceed 500 characters")
        String address,

        @NotEmpty(message = "Order must contain at least one product")
        List<@Valid OrderItemRequest> items
) {

    public record OrderItemRequest(

            @jakarta.validation.constraints.NotNull(
                    message = "Product ID is required"
            )
            Long productId,

            @jakarta.validation.constraints.NotNull(
                    message = "Quantity is required"
            )
            @jakarta.validation.constraints.Min(
                    value = 1,
                    message = "Quantity must be at least 1"
            )
            Integer quantity
    ) {
    }
}