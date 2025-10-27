package com.coffeeshop.api.controller;

import com.coffeeshop.application.dto.CreateOrderRequest;
import com.coffeeshop.application.dto.OrderDto;
import com.coffeeshop.application.service.OrderService;
import com.coffeeshop.domain.order.OrderStatus;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.time.Instant;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/orders")
@SecurityRequirement(name = "bearer-jwt")
@Tag(name = "Orders", description = "Order management endpoints")
public class OrderController {
    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @PostMapping
    @Operation(summary = "Create order", description = "Create a new order with multiple items")
    public ResponseEntity<OrderDto> createOrder(
            @Valid @RequestBody CreateOrderRequest request, Authentication authentication) {
        String customerId = authentication.getName();
        OrderDto created = orderService.createOrder(request, customerId);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get order by ID", description = "Retrieve an order by its ID")
    public ResponseEntity<OrderDto> getOrder(@PathVariable UUID id) {
        OrderDto order = orderService.getOrder(id);
        return ResponseEntity.ok(order);
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(
            summary = "Get all orders",
            description =
                    "Retrieve all orders with pagination, sorting, and filtering (Admin only)")
    public ResponseEntity<Page<OrderDto>> getAllOrders(
            @RequestParam(required = false) OrderStatus status,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
                    Instant dateFrom,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
                    Instant dateTo,
            @PageableDefault(size = 20, sort = "createdAt", direction = Sort.Direction.DESC)
                    Pageable pageable) {
        Page<OrderDto> orders;

        if (status != null && dateFrom != null && dateTo != null) {
            orders = orderService.getOrdersByDateRange(dateFrom, dateTo, pageable);
        } else if (status != null) {
            orders = orderService.getOrdersByStatus(status, pageable);
        } else if (dateFrom != null && dateTo != null) {
            orders = orderService.getOrdersByDateRange(dateFrom, dateTo, pageable);
        } else {
            orders = orderService.getAllOrders(pageable);
        }

        return ResponseEntity.ok(orders);
    }

    @PostMapping("/{id}/pay")
    @Operation(summary = "Pay order", description = "Process payment for an order")
    public ResponseEntity<OrderDto> payOrder(@PathVariable UUID id) {
        OrderDto order = orderService.payOrder(id);
        return ResponseEntity.ok(order);
    }

    @PostMapping("/{id}/cancel")
    @Operation(summary = "Cancel order", description = "Cancel an order")
    public ResponseEntity<OrderDto> cancelOrder(@PathVariable UUID id) {
        OrderDto order = orderService.cancelOrder(id);
        return ResponseEntity.ok(order);
    }
}



