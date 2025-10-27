package com.coffeeshop.domain.order;

import com.coffeeshop.domain.shared.Money;
import java.util.UUID;

public class OrderItem {
    private UUID id;
    private UUID productId;
    private String productName;
    private Money unitPrice;
    private int quantity;
    private Money subtotal;

    public OrderItem() {}

    public OrderItem(
            UUID id, UUID productId, String productName, Money unitPrice, int quantity) {
        this.id = id;
        this.productId = productId;
        this.productName = productName;
        this.unitPrice = unitPrice;
        this.quantity = quantity;
        this.subtotal = unitPrice.multiply(quantity);
    }

    public static OrderItem create(UUID productId, String productName, Money unitPrice, int quantity) {
        if (productId == null) {
            throw new IllegalArgumentException("Product ID cannot be null");
        }
        if (quantity <= 0) {
            throw new IllegalArgumentException("Quantity must be positive");
        }
        return new OrderItem(UUID.randomUUID(), productId, productName, unitPrice, quantity);
    }

    public Money getSubtotal() {
        return unitPrice.multiply(quantity);
    }

    // Getters and setters
    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public UUID getProductId() {
        return productId;
    }

    public void setProductId(UUID productId) {
        this.productId = productId;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public Money getUnitPrice() {
        return unitPrice;
    }

    public void setUnitPrice(Money unitPrice) {
        this.unitPrice = unitPrice;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public void setSubtotal(Money subtotal) {
        this.subtotal = subtotal;
    }
}



