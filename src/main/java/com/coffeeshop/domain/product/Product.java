package com.coffeeshop.domain.product;

import com.coffeeshop.domain.shared.Money;
import java.time.Instant;
import java.util.UUID;

public class Product {
    private UUID id;
    private String name;
    private String sku;
    private Money price;
    private int stockQuantity;
    private Instant createdAt;
    private Instant updatedAt;

    public Product() {}

    public Product(UUID id, String name, String sku, Money price, int stockQuantity) {
        this.id = id;
        this.name = name;
        this.sku = sku;
        this.price = price;
        this.stockQuantity = stockQuantity;
        this.createdAt = Instant.now();
        this.updatedAt = Instant.now();
    }

    public static Product create(String name, String sku, Money price, int initialStock) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Product name cannot be empty");
        }
        if (sku == null || sku.isBlank()) {
            throw new IllegalArgumentException("Product SKU cannot be empty");
        }
        if (price == null) {
            throw new IllegalArgumentException("Product price cannot be null");
        }
        if (initialStock < 0) {
            throw new IllegalArgumentException("Initial stock cannot be negative");
        }

        return new Product(UUID.randomUUID(), name, sku, price, initialStock);
    }

    public void restock(int quantity) {
        if (quantity <= 0) {
            throw new IllegalArgumentException("Restock quantity must be positive");
        }
        this.stockQuantity += quantity;
        this.updatedAt = Instant.now();
    }

    public void decreaseStock(int quantity) {
        if (quantity <= 0) {
            throw new IllegalArgumentException("Quantity must be positive");
        }
        if (this.stockQuantity < quantity) {
            throw new IllegalStateException(
                    "Insufficient stock for product: " + name + ". Available: " + stockQuantity
                            + ", Requested: " + quantity);
        }
        this.stockQuantity -= quantity;
        this.updatedAt = Instant.now();
    }

    public void increaseStock(int quantity) {
        if (quantity <= 0) {
            throw new IllegalArgumentException("Quantity must be positive");
        }
        this.stockQuantity += quantity;
        this.updatedAt = Instant.now();
    }

    public boolean isLowStock(int threshold) {
        return this.stockQuantity < threshold;
    }

    public void updatePrice(Money newPrice) {
        if (newPrice == null) {
            throw new IllegalArgumentException("Price cannot be null");
        }
        this.price = newPrice;
        this.updatedAt = Instant.now();
    }

    // Getters and setters
    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSku() {
        return sku;
    }

    public void setSku(String sku) {
        this.sku = sku;
    }

    public Money getPrice() {
        return price;
    }

    public void setPrice(Money price) {
        this.price = price;
    }

    public int getStockQuantity() {
        return stockQuantity;
    }

    public void setStockQuantity(int stockQuantity) {
        this.stockQuantity = stockQuantity;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Instant updatedAt) {
        this.updatedAt = updatedAt;
    }
}



