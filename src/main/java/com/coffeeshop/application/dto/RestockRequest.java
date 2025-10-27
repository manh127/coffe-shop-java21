package com.coffeeshop.application.dto;

import jakarta.validation.constraints.Min;

public record RestockRequest(
        @Min(value = 1, message = "Restock quantity must be at least 1") int quantity) {}



