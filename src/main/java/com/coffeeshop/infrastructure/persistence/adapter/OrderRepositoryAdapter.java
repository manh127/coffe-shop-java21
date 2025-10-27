package com.coffeeshop.infrastructure.persistence.adapter;

import com.coffeeshop.domain.order.Order;
import com.coffeeshop.domain.order.OrderRepository;
import com.coffeeshop.domain.order.OrderStatus;
import com.coffeeshop.infrastructure.persistence.entity.OrderEntity;
import com.coffeeshop.infrastructure.persistence.jpa.JpaOrderRepository;
import java.time.Instant;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

@Repository
public class OrderRepositoryAdapter implements OrderRepository {
    private final JpaOrderRepository jpaRepository;

    public OrderRepositoryAdapter(JpaOrderRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public Order save(Order order) {
        OrderEntity entity = OrderEntity.fromDomain(order);
        OrderEntity saved = jpaRepository.save(entity);
        return saved.toDomain();
    }

    @Override
    public Optional<Order> findById(UUID id) {
        return jpaRepository.findById(id).map(OrderEntity::toDomain);
    }

    @Override
    public Optional<Order> findByOrderNumber(String orderNumber) {
        return jpaRepository.findByOrderNumber(orderNumber).map(OrderEntity::toDomain);
    }

    @Override
    public Page<Order> findAll(Pageable pageable) {
        return jpaRepository.findAll(pageable).map(OrderEntity::toDomain);
    }

    @Override
    public Page<Order> findByCustomerId(String customerId, Pageable pageable) {
        return jpaRepository.findByCustomerId(customerId, pageable).map(OrderEntity::toDomain);
    }

    @Override
    public Page<Order> findByStatus(OrderStatus status, Pageable pageable) {
        return jpaRepository.findByStatus(status, pageable).map(OrderEntity::toDomain);
    }

    @Override
    public Page<Order> findByCreatedAtBetween(Instant start, Instant end, Pageable pageable) {
        return jpaRepository.findByCreatedAtBetween(start, end, pageable)
                .map(OrderEntity::toDomain);
    }

    @Override
    public Page<Order> findByStatusAndCreatedAtBetween(
            OrderStatus status, Instant start, Instant end, Pageable pageable) {
        return jpaRepository.findByStatusAndCreatedAtBetween(status, start, end, pageable)
                .map(OrderEntity::toDomain);
    }
}



