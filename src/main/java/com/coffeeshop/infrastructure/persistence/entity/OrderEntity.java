package com.coffeeshop.infrastructure.persistence.entity;

import com.coffeeshop.domain.order.Order;
import com.coffeeshop.domain.order.OrderItem;
import com.coffeeshop.domain.order.OrderStatus;
import com.coffeeshop.domain.shared.Money;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Entity
@Table(
        name = "orders",
        indexes = {
            @Index(name = "idx_order_number", columnList = "order_number", unique = true),
            @Index(name = "idx_order_customer", columnList = "customer_id"),
            @Index(name = "idx_order_status", columnList = "status"),
            @Index(name = "idx_order_created_at", columnList = "created_at")
        })
public class OrderEntity {
    @Id
    @Column(name = "id", columnDefinition = "UUID")
    private UUID id;

    @Column(name = "order_number", nullable = false, unique = true, length = 50)
    private String orderNumber;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    @JoinColumn(name = "order_id", nullable = false)
    private List<OrderItemEntity> items = new ArrayList<>();

    @Column(name = "total_amount", nullable = false, precision = 10, scale = 2)
    private BigDecimal totalAmount;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private OrderStatus status;

    @Column(name = "customer_id", nullable = false, length = 255)
    private String customerId;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    public OrderEntity() {}

    public static OrderEntity fromDomain(Order order) {
        OrderEntity entity = new OrderEntity();
        entity.setId(order.getId());
        entity.setOrderNumber(order.getOrderNumber());
        entity.setTotalAmount(order.getTotalAmount().amount());
        entity.setStatus(order.getStatus());
        entity.setCustomerId(order.getCustomerId());
        entity.setCreatedAt(order.getCreatedAt());
        entity.setUpdatedAt(order.getUpdatedAt());

        List<OrderItemEntity> itemEntities = order.getItems().stream()
                .map(OrderItemEntity::fromDomain)
                .collect(Collectors.toList());
        entity.setItems(itemEntities);

        return entity;
    }

    public Order toDomain() {
        Order order = new Order();
        order.setId(this.id);
        order.setOrderNumber(this.orderNumber);
        order.setTotalAmount(Money.of(this.totalAmount));
        order.setStatus(this.status);
        order.setCustomerId(this.customerId);
        order.setCreatedAt(this.createdAt);
        order.setUpdatedAt(this.updatedAt);

        List<OrderItem> domainItems = this.items.stream()
                .map(OrderItemEntity::toDomain)
                .collect(Collectors.toList());
        order.setItems(domainItems);

        return order;
    }

    @PrePersist
    protected void onCreate() {
        if (createdAt == null) {
            createdAt = Instant.now();
        }
        updatedAt = Instant.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = Instant.now();
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

    public List<OrderItemEntity> getItems() {
        return items;
    }

    public void setItems(List<OrderItemEntity> items) {
        this.items = items;
    }

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(BigDecimal totalAmount) {
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



