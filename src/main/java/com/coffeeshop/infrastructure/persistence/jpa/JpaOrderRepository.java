package com.coffeeshop.infrastructure.persistence.jpa;

import com.coffeeshop.domain.order.OrderStatus;
import com.coffeeshop.infrastructure.persistence.entity.OrderEntity;
import java.time.Instant;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface JpaOrderRepository extends JpaRepository<OrderEntity, UUID> {
    Optional<OrderEntity> findByOrderNumber(String orderNumber);

    Page<OrderEntity> findByCustomerId(String customerId, Pageable pageable);

    Page<OrderEntity> findByStatus(OrderStatus status, Pageable pageable);

    Page<OrderEntity> findByCreatedAtBetween(Instant start, Instant end, Pageable pageable);

    Page<OrderEntity> findByStatusAndCreatedAtBetween(
            OrderStatus status, Instant start, Instant end, Pageable pageable);
}



