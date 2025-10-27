package com.coffeeshop.application.service;

import com.coffeeshop.application.dto.CreateOrderRequest;
import com.coffeeshop.application.dto.OrderDto;
import com.coffeeshop.application.exception.BusinessException;
import com.coffeeshop.application.exception.InsufficientStockException;
import com.coffeeshop.application.exception.ResourceNotFoundException;
import com.coffeeshop.application.mapper.OrderMapper;
import com.coffeeshop.domain.order.Order;
import com.coffeeshop.domain.order.OrderItem;
import com.coffeeshop.domain.order.OrderRepository;
import com.coffeeshop.domain.order.OrderStatus;
import com.coffeeshop.domain.product.Product;
import com.coffeeshop.domain.product.ProductRepository;
import java.time.Instant;
import java.util.*;
import java.util.concurrent.StructuredTaskScope;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class OrderService {
    private static final Logger log = LoggerFactory.getLogger(OrderService.class);

    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;
    private final OrderMapper orderMapper;
    private final DiscountService discountService;
    private final ShippingService shippingService;

    public OrderService(
            OrderRepository orderRepository,
            ProductRepository productRepository,
            OrderMapper orderMapper,
            DiscountService discountService,
            ShippingService shippingService) {
        this.orderRepository = orderRepository;
        this.productRepository = productRepository;
        this.orderMapper = orderMapper;
        this.discountService = discountService;
        this.shippingService = shippingService;
    }

    /**
     * Demonstrates Virtual Threads with Structured Concurrency
     * This method runs multiple I/O-bound tasks in parallel using StructuredTaskScope
     */
    @Transactional
    public OrderDto createOrder(CreateOrderRequest request, String customerId) {
        log.info("Creating order for customer: {} with {} items", customerId, request.items().size());

        // Extract product IDs
        List<UUID> productIds = request.items().stream()
                .map(CreateOrderRequest.OrderItemRequest::productId)
                .toList();

        // Using Virtual Threads with Structured Concurrency
        // Run multiple tasks in parallel and wait for all to complete
        try (var scope = new StructuredTaskScope.ShutdownOnFailure()) {
            // Fork task 1: Load products
            var productsFuture = scope.fork(() -> {
                log.info("Loading products on virtual thread: {}", Thread.currentThread());
                return loadProducts(productIds);
            });

            // Fork task 2: Calculate discount (simulated I/O)
            var discountFuture = scope.fork(() -> {
                log.info("Calculating discount on virtual thread: {}", Thread.currentThread());
                return discountService.calculateDiscount(customerId);
            });

            // Fork task 3: Get shipping estimate (simulated I/O)
            var shippingFuture = scope.fork(() -> {
                log.info("Calculating shipping on virtual thread: {}", Thread.currentThread());
                return shippingService.estimateShipping(customerId);
            });

            // Wait for all tasks to complete or fail
            scope.join();
            scope.throwIfFailed();

            // Get results from all tasks
            Map<UUID, Product> productsMap = productsFuture.get();
            double discountPercentage = discountFuture.get();
            double shippingCost = shippingFuture.get();

            log.info("All parallel tasks completed. Discount: {}%, Shipping: ${}",
                    discountPercentage, shippingCost);

            // Validate stock and create order items
            List<OrderItem> orderItems = new ArrayList<>();
            for (CreateOrderRequest.OrderItemRequest itemRequest : request.items()) {
                Product product = productsMap.get(itemRequest.productId());
                if (product == null) {
                    throw new ResourceNotFoundException(
                            "Product not found: " + itemRequest.productId());
                }

                if (product.getStockQuantity() < itemRequest.quantity()) {
                    throw new InsufficientStockException(
                            product.getName(),
                            product.getStockQuantity(),
                            itemRequest.quantity());
                }

                OrderItem item = OrderItem.create(
                        product.getId(),
                        product.getName(),
                        product.getPrice(),
                        itemRequest.quantity());
                orderItems.add(item);
            }

            // Create and save order
            Order order = Order.create(customerId, orderItems);
            Order savedOrder = orderRepository.save(order);

            log.info("Order created with ID: {} and number: {}",
                    savedOrder.getId(), savedOrder.getOrderNumber());

            return orderMapper.toDto(savedOrder);

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new BusinessException("Order creation was interrupted");
        } catch (Exception e) {
            log.error("Error creating order", e);
            throw new BusinessException("Failed to create order: " + e.getMessage());
        }
    }

    private Map<UUID, Product> loadProducts(List<UUID> productIds) {
        List<Product> products = productRepository.findAllById(productIds);
        return products.stream().collect(Collectors.toMap(Product::getId, p -> p));
    }

    @Transactional(readOnly = true)
    public OrderDto getOrder(UUID id) {
        Order order = orderRepository
                .findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Order", id));
        return orderMapper.toDto(order);
    }

    @Transactional(readOnly = true)
    public Page<OrderDto> getAllOrders(Pageable pageable) {
        return orderRepository.findAll(pageable).map(orderMapper::toDto);
    }

    @Transactional(readOnly = true)
    public Page<OrderDto> getOrdersByStatus(OrderStatus status, Pageable pageable) {
        return orderRepository.findByStatus(status, pageable).map(orderMapper::toDto);
    }

    @Transactional(readOnly = true)
    public Page<OrderDto> getOrdersByDateRange(
            Instant startDate, Instant endDate, Pageable pageable) {
        return orderRepository.findByCreatedAtBetween(startDate, endDate, pageable)
                .map(orderMapper::toDto);
    }

    @Transactional
    public OrderDto payOrder(UUID orderId) {
        log.info("Processing payment for order: {}", orderId);

        Order order = orderRepository
                .findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order", orderId));

        if (!order.isCreated()) {
            throw new BusinessException(
                    "Order cannot be paid. Current status: " + order.getStatus(),
                    "INVALID_ORDER_STATUS");
        }

        // Deduct stock for each item
        for (OrderItem item : order.getItems()) {
            Product product = productRepository
                    .findById(item.getProductId())
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "Product", item.getProductId()));

            product.decreaseStock(item.getQuantity());
            productRepository.save(product);
        }

        order.pay();
        Order savedOrder = orderRepository.save(order);

        log.info("Order {} paid successfully", orderId);
        return orderMapper.toDto(savedOrder);
    }

    @Transactional
    public OrderDto cancelOrder(UUID orderId) {
        log.info("Canceling order: {}", orderId);

        Order order = orderRepository
                .findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order", orderId));

        if (order.isCanceled()) {
            throw new BusinessException("Order is already canceled", "ORDER_ALREADY_CANCELED");
        }

        // If order was not paid yet, we don't need to restore stock
        // If it was paid, restore the stock
        if (order.isPaid()) {
            for (OrderItem item : order.getItems()) {
                Product product = productRepository
                        .findById(item.getProductId())
                        .orElseThrow(() -> new ResourceNotFoundException(
                                "Product", item.getProductId()));

                product.increaseStock(item.getQuantity());
                productRepository.save(product);
            }
        }

        order.cancel();
        Order savedOrder = orderRepository.save(order);

        log.info("Order {} canceled successfully", orderId);
        return orderMapper.toDto(savedOrder);
    }
}



