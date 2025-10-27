package com.coffeeshop.domain.product;

import static org.assertj.core.api.Assertions.*;

import com.coffeeshop.domain.shared.Money;
import org.junit.jupiter.api.Test;

class ProductTest {
    @Test
    void shouldCreateValidProduct() {
        Product product = Product.create("Espresso", "COFFEE-ESP-001", Money.of("2.50"), 100);

        assertThat(product.getName()).isEqualTo("Espresso");
        assertThat(product.getSku()).isEqualTo("COFFEE-ESP-001");
        assertThat(product.getPrice().amount()).isEqualByComparingTo("2.50");
        assertThat(product.getStockQuantity()).isEqualTo(100);
    }

    @Test
    void shouldThrowExceptionWhenCreatingProductWithEmptyName() {
        assertThatThrownBy(() -> Product.create("", "SKU-001", Money.of("10.00"), 100))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("name cannot be empty");
    }

    @Test
    void shouldRestockProduct() {
        Product product = Product.create("Coffee", "SKU-001", Money.of("5.00"), 50);
        product.restock(25);

        assertThat(product.getStockQuantity()).isEqualTo(75);
    }

    @Test
    void shouldThrowExceptionWhenRestockingWithNegativeQuantity() {
        Product product = Product.create("Coffee", "SKU-001", Money.of("5.00"), 50);

        assertThatThrownBy(() -> product.restock(-10))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("must be positive");
    }

    @Test
    void shouldDecreaseStock() {
        Product product = Product.create("Coffee", "SKU-001", Money.of("5.00"), 50);
        product.decreaseStock(20);

        assertThat(product.getStockQuantity()).isEqualTo(30);
    }

    @Test
    void shouldThrowExceptionWhenDecreasingStockBeyondAvailable() {
        Product product = Product.create("Coffee", "SKU-001", Money.of("5.00"), 10);

        assertThatThrownBy(() -> product.decreaseStock(20))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("Insufficient stock");
    }

    @Test
    void shouldDetectLowStock() {
        Product product = Product.create("Coffee", "SKU-001", Money.of("5.00"), 5);

        assertThat(product.isLowStock(10)).isTrue();
        assertThat(product.isLowStock(3)).isFalse();
    }
}



