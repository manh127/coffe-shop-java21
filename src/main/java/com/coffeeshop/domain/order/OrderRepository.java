package com.coffeeshop.domain.order;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface OrderRepository {
    Order save(Order order);

    Optional<Order> findById(UUID id);

    Optional<Order> findByOrderNumber(String orderNumber);

    Page<Order> findAll(Pageable pageable);

    Page<Order> findByCustomerId(String customerId, Pageable pageable);

    Page<Order> findByStatus(OrderStatus status, Pageable pageable);

    Page<Order> findByCreatedAtBetween(Instant start, Instant end, Pageable pageable);

    Page<Order> findByStatusAndCreatedAtBetween(
            OrderStatus status, Instant start, Instant end, Pageable pageable);
}



