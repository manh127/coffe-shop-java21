package com.coffeeshop.domain.order;

import com.coffeeshop.domain.shared.Money;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

public class Order {
    private UUID id;
    private String orderNumber;
    private List<OrderItem> items;
    private Money totalAmount;
    private OrderStatus status;
    private String customerId;
    private Instant createdAt;
    private Instant updatedAt;

    public Order() {
        this.items = new ArrayList<>();
    }

    public Order(
            UUID id,
            String orderNumber,
            List<OrderItem> items,
            String customerId,
            OrderStatus status) {
        this.id = id;
        this.orderNumber = orderNumber;
        this.items = new ArrayList<>(items);
        this.customerId = customerId;
        this.status = status;
        this.totalAmount = calculateTotal();
        this.createdAt = Instant.now();
        this.updatedAt = Instant.now();
    }

    public static Order create(String customerId, List<OrderItem> items) {
        if (customerId == null || customerId.isBlank()) {
            throw new IllegalArgumentException("Customer ID cannot be empty");
        }
        if (items == null || items.isEmpty()) {
            throw new IllegalArgumentException("Order must have at least one item");
        }

        UUID orderId = UUID.randomUUID();
        String orderNumber = generateOrderNumber(orderId);
        return new Order(orderId, orderNumber, items, customerId, OrderStatus.CREATED);
    }

    private static String generateOrderNumber(UUID orderId) {
        return "ORD-" + orderId.toString().substring(0, 8).toUpperCase();
    }

    private Money calculateTotal() {
        return items.stream()
                .map(OrderItem::getSubtotal)
                .reduce(Money.zero(), Money::add);
    }

    public void pay() {
        if (this.status != OrderStatus.CREATED) {
            throw new IllegalStateException(
                    "Cannot pay order with status: " + this.status);
        }
        this.status = OrderStatus.PAID;
        this.updatedAt = Instant.now();
    }

    public void cancel() {
        if (this.status == OrderStatus.CANCELED) {
            throw new IllegalStateException("Order is already canceled");
        }
        this.status = OrderStatus.CANCELED;
        this.updatedAt = Instant.now();
    }

    public boolean isPaid() {
        return this.status == OrderStatus.PAID;
    }

    public boolean isCanceled() {
        return this.status == OrderStatus.CANCELED;
    }

    public boolean isCreated() {
        return this.status == OrderStatus.CREATED;
    }

    // Getters and setters
    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getOrderNumber() {
        return orderNumber;
    }

    public void setOrderNumber(String orderNumber) {
        this.orderNumber = orderNumber;
    }

    public List<OrderItem> getItems() {
        return Collections.unmodifiableList(items);
    }

    public void setItems(List<OrderItem> items) {
        this.items = items;
        this.totalAmount = calculateTotal();
    }

    public Money getTotalAmount() {
        if (totalAmount == null) {
            totalAmount = calculateTotal();
        }
        return totalAmount;
    }

    public void setTotalAmount(Money totalAmount) {
        this.totalAmount = totalAmount;
    }

    public OrderStatus getStatus() {
        return status;
    }

    public void setStatus(OrderStatus status) {
        this.status = status;
    }

    public String getCustomerId() {
        return customerId;
    }

    public void setCustomerId(String customerId) {
        this.customerId = customerId;
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



