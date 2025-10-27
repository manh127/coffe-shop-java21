package com.coffeeshop.infrastructure.persistence.entity;

import com.coffeeshop.domain.order.OrderItem;
import com.coffeeshop.domain.shared.Money;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Table(name = "order_items")
public class OrderItemEntity {
    @Id
    @Column(name = "id", columnDefinition = "UUID")
    private UUID id;

    @Column(name = "order_id", columnDefinition = "UUID")
    private UUID orderId;

    @Column(name = "product_id", nullable = false, columnDefinition = "UUID")
    private UUID productId;

    @Column(name = "product_name", nullable = false, length = 255)
    private String productName;

    @Column(name = "unit_price", nullable = false, precision = 10, scale = 2)
    private BigDecimal unitPrice;

    @Column(name = "quantity", nullable = false)
    private int quantity;

    @Column(name = "subtotal", nullable = false, precision = 10, scale = 2)
    private BigDecimal subtotal;

    public OrderItemEntity() {}

    public static OrderItemEntity fromDomain(OrderItem item) {
        OrderItemEntity entity = new OrderItemEntity();
        entity.setId(item.getId());
        entity.setProductId(item.getProductId());
        entity.setProductName(item.getProductName());
        entity.setUnitPrice(item.getUnitPrice().amount());
        entity.setQuantity(item.getQuantity());
        entity.setSubtotal(item.getSubtotal().amount());
        return entity;
    }

    public OrderItem toDomain() {
        OrderItem item = new OrderItem();
        item.setId(this.id);
        item.setProductId(this.productId);
        item.setProductName(this.productName);
        item.setUnitPrice(Money.of(this.unitPrice));
        item.setQuantity(this.quantity);
        item.setSubtotal(Money.of(this.subtotal));
        return item;
    }

    // Getters and setters
    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public UUID getOrderId() {
        return orderId;
    }

    public void setOrderId(UUID orderId) {
        this.orderId = orderId;
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

    public BigDecimal getUnitPrice() {
        return unitPrice;
    }

    public void setUnitPrice(BigDecimal unitPrice) {
        this.unitPrice = unitPrice;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public BigDecimal getSubtotal() {
        return subtotal;
    }

    public void setSubtotal(BigDecimal subtotal) {
        this.subtotal = subtotal;
    }
}



