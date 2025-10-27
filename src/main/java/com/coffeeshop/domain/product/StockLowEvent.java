package com.coffeeshop.domain.product;

import com.coffeeshop.domain.shared.DomainEvent;
import java.time.Instant;
import java.util.UUID;

public record StockLowEvent(
        UUID productId, String productName, int currentStock, int threshold, Instant occurredAt)
        implements DomainEvent {
    public static StockLowEvent of(Product product, int threshold) {
        return new StockLowEvent(
                product.getId(),
                product.getName(),
                product.getStockQuantity(),
                threshold,
                Instant.now());
    }
}



