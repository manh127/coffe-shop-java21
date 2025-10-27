package com.coffeeshop.domain.order;

import static org.assertj.core.api.Assertions.*;

import com.coffeeshop.domain.shared.Money;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.Test;

class OrderTest {
    @Test
    void shouldCreateValidOrder() {
        List<OrderItem> items = Arrays.asList(
                OrderItem.create(UUID.randomUUID(), "Product 1", Money.of("10.00"), 2),
                OrderItem.create(UUID.randomUUID(), "Product 2", Money.of("5.00"), 3));

        Order order = Order.create("customer@test.com", items);

        assertThat(order.getCustomerId()).isEqualTo("customer@test.com");
        assertThat(order.getItems()).hasSize(2);
        assertThat(order.getStatus()).isEqualTo(OrderStatus.CREATED);
        assertThat(order.getTotalAmount().amount()).isEqualByComparingTo("35.00");
    }

    @Test
    void shouldThrowExceptionWhenCreatingOrderWithEmptyItems() {
        assertThatThrownBy(() -> Order.create("customer@test.com", Collections.emptyList()))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("at least one item");
    }

    @Test
    void shouldPayOrder() {
        OrderItem item = OrderItem.create(UUID.randomUUID(), "Product", Money.of("10.00"), 1);
        Order order = Order.create("customer@test.com", List.of(item));

        order.pay();

        assertThat(order.getStatus()).isEqualTo(OrderStatus.PAID);
        assertThat(order.isPaid()).isTrue();
    }

    @Test
    void shouldThrowExceptionWhenPayingNonCreatedOrder() {
        OrderItem item = OrderItem.create(UUID.randomUUID(), "Product", Money.of("10.00"), 1);
        Order order = Order.create("customer@test.com", List.of(item));
        order.pay();

        assertThatThrownBy(() -> order.pay())
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("Cannot pay order");
    }

    @Test
    void shouldCancelOrder() {
        OrderItem item = OrderItem.create(UUID.randomUUID(), "Product", Money.of("10.00"), 1);
        Order order = Order.create("customer@test.com", List.of(item));

        order.cancel();

        assertThat(order.getStatus()).isEqualTo(OrderStatus.CANCELED);
        assertThat(order.isCanceled()).isTrue();
    }

    @Test
    void shouldThrowExceptionWhenCancelingAlreadyCanceledOrder() {
        OrderItem item = OrderItem.create(UUID.randomUUID(), "Product", Money.of("10.00"), 1);
        Order order = Order.create("customer@test.com", List.of(item));
        order.cancel();

        assertThatThrownBy(() -> order.cancel())
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("already canceled");
    }

    @Test
    void shouldCalculateTotalAmount() {
        List<OrderItem> items = Arrays.asList(
                OrderItem.create(UUID.randomUUID(), "Product 1", Money.of("12.50"), 2),
                OrderItem.create(UUID.randomUUID(), "Product 2", Money.of("7.25"), 3));

        Order order = Order.create("customer@test.com", items);

        // (12.50 * 2) + (7.25 * 3) = 25.00 + 21.75 = 46.75
        assertThat(order.getTotalAmount().amount()).isEqualByComparingTo("46.75");
    }
}



