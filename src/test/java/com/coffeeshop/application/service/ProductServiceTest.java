package com.coffeeshop.application.service;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import com.coffeeshop.application.dto.CreateProductRequest;
import com.coffeeshop.application.dto.ProductDto;
import com.coffeeshop.application.exception.BusinessException;
import com.coffeeshop.application.exception.ResourceNotFoundException;
import com.coffeeshop.application.mapper.ProductMapper;
import com.coffeeshop.domain.product.Product;
import com.coffeeshop.domain.product.ProductRepository;
import com.coffeeshop.domain.shared.Money;
import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ProductServiceTest {
    @Mock
    private ProductRepository productRepository;

    @Mock
    private ProductMapper productMapper;

    @InjectMocks
    private ProductService productService;

    @Test
    void shouldCreateProduct() {
        CreateProductRequest request =
                new CreateProductRequest("Espresso", "COFFEE-ESP-001", BigDecimal.valueOf(2.50), 100);

        Product product = Product.create("Espresso", "COFFEE-ESP-001", Money.of("2.50"), 100);
        ProductDto dto = new ProductDto(
                product.getId(), "Espresso", "COFFEE-ESP-001", BigDecimal.valueOf(2.50), 100, null, null);

        when(productRepository.existsBySku("COFFEE-ESP-001")).thenReturn(false);
        when(productRepository.save(any(Product.class))).thenReturn(product);
        when(productMapper.toDto(any(Product.class))).thenReturn(dto);

        ProductDto result = productService.createProduct(request);

        assertThat(result.name()).isEqualTo("Espresso");
        verify(productRepository).save(any(Product.class));
    }

    @Test
    void shouldThrowExceptionWhenCreatingProductWithDuplicateSku() {
        CreateProductRequest request =
                new CreateProductRequest("Espresso", "COFFEE-ESP-001", BigDecimal.valueOf(2.50), 100);

        when(productRepository.existsBySku("COFFEE-ESP-001")).thenReturn(true);

        assertThatThrownBy(() -> productService.createProduct(request))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("already exists");

        verify(productRepository, never()).save(any(Product.class));
    }

    @Test
    void shouldGetProductById() {
        UUID productId = UUID.randomUUID();
        Product product = Product.create("Espresso", "COFFEE-ESP-001", Money.of("2.50"), 100);
        product.setId(productId);

        ProductDto dto = new ProductDto(
                productId, "Espresso", "COFFEE-ESP-001", BigDecimal.valueOf(2.50), 100, null, null);

        when(productRepository.findById(productId)).thenReturn(Optional.of(product));
        when(productMapper.toDto(product)).thenReturn(dto);

        ProductDto result = productService.getProduct(productId);

        assertThat(result.id()).isEqualTo(productId);
        assertThat(result.name()).isEqualTo("Espresso");
    }

    @Test
    void shouldThrowExceptionWhenProductNotFound() {
        UUID productId = UUID.randomUUID();
        when(productRepository.findById(productId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> productService.getProduct(productId))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void shouldRestockProduct() {
        UUID productId = UUID.randomUUID();
        Product product = Product.create("Espresso", "COFFEE-ESP-001", Money.of("2.50"), 50);
        product.setId(productId);

        when(productRepository.findById(productId)).thenReturn(Optional.of(product));
        when(productRepository.save(any(Product.class))).thenReturn(product);
        when(productMapper.toDto(any(Product.class)))
                .thenReturn(new ProductDto(
                        productId, "Espresso", "COFFEE-ESP-001", BigDecimal.valueOf(2.50), 100, null, null));

        ProductDto result = productService.restockProduct(productId, 50);

        assertThat(result.stockQuantity()).isEqualTo(100);
        verify(productRepository).save(any(Product.class));
    }
}



