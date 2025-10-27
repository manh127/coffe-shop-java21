package com.coffeeshop.infrastructure.persistence;

import static org.assertj.core.api.Assertions.*;

import com.coffeeshop.domain.product.Product;
import com.coffeeshop.domain.product.ProductRepository;
import com.coffeeshop.domain.shared.Money;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

@SpringBootTest
@Testcontainers
@ActiveProfiles("test")
@Transactional
class ProductRepositoryIT {
    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:15-alpine")
            .withDatabaseName("testdb")
            .withUsername("test")
            .withPassword("test");

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
    }

    @Autowired
    private ProductRepository productRepository;

    @Test
    void shouldSaveAndFindProduct() {
        Product product = Product.create("Test Product", "TEST-SKU-001", Money.of("10.00"), 50);
        Product saved = productRepository.save(product);

        Optional<Product> found = productRepository.findById(saved.getId());

        assertThat(found).isPresent();
        assertThat(found.get().getName()).isEqualTo("Test Product");
        assertThat(found.get().getSku()).isEqualTo("TEST-SKU-001");
    }

    @Test
    void shouldFindProductBySku() {
        Product product = Product.create("Another Product", "UNIQUE-SKU-002", Money.of("15.00"), 30);
        productRepository.save(product);

        Optional<Product> found = productRepository.findBySku("UNIQUE-SKU-002");

        assertThat(found).isPresent();
        assertThat(found.get().getName()).isEqualTo("Another Product");
    }

    @Test
    void shouldFindLowStockProducts() {
        Product product1 = Product.create("Low Stock 1", "LOW-001", Money.of("5.00"), 3);
        Product product2 = Product.create("Low Stock 2", "LOW-002", Money.of("6.00"), 7);
        Product product3 = Product.create("High Stock", "HIGH-001", Money.of("7.00"), 50);

        productRepository.save(product1);
        productRepository.save(product2);
        productRepository.save(product3);

        List<Product> lowStockProducts = productRepository.findLowStockProducts(10);

        assertThat(lowStockProducts).hasSize(2);
        assertThat(lowStockProducts)
                .extracting(Product::getSku)
                .containsExactlyInAnyOrder("LOW-001", "LOW-002");
    }
}



