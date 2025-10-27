package com.coffeeshop.application.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;

public record CreateProductRequest(
        @NotBlank(message = "Product name is required") String name,
        @NotBlank(message = "Product SKU is required") String sku,
        @NotNull(message = "Price is required") @Min(value = 0, message = "Price must be positive")
                BigDecimal price,
        @Min(value = 0, message = "Initial stock cannot be negative") int initialStock) {}



