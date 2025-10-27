package com.coffeeshop.application.dto;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

public record OrderDto(
        UUID id,
        String orderNumber,
        List<OrderItemDto> items,
        BigDecimal totalAmount,
        String status,
        String customerId,
        Instant createdAt,
        Instant updatedAt) {}



