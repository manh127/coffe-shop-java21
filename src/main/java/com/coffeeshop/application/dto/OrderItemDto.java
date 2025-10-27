package com.coffeeshop.application.dto;

import java.math.BigDecimal;
import java.util.UUID;

public record OrderItemDto(
        UUID id, UUID productId, String productName, BigDecimal unitPrice, int quantity, BigDecimal subtotal) {}



