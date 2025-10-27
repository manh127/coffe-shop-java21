package com.coffeeshop.application.service;

import com.coffeeshop.application.dto.CreateProductRequest;
import com.coffeeshop.application.dto.ProductDto;
import com.coffeeshop.application.exception.BusinessException;
import com.coffeeshop.application.exception.ResourceNotFoundException;
import com.coffeeshop.application.mapper.ProductMapper;
import com.coffeeshop.domain.product.Product;
import com.coffeeshop.domain.product.ProductRepository;
import com.coffeeshop.domain.shared.Money;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ProductService {
    private static final Logger log = LoggerFactory.getLogger(ProductService.class);

    private final ProductRepository productRepository;
    private final ProductMapper productMapper;

    public ProductService(ProductRepository productRepository, ProductMapper productMapper) {
        this.productRepository = productRepository;
        this.productMapper = productMapper;
    }

    @Transactional
    public ProductDto createProduct(CreateProductRequest request) {
        log.info("Creating product with SKU: {}", request.sku());

        if (productRepository.existsBySku(request.sku())) {
            throw new BusinessException("Product with SKU " + request.sku() + " already exists",
                    "DUPLICATE_SKU");
        }

        Product product = Product.create(
                request.name(),
                request.sku(),
                Money.of(request.price()),
                request.initialStock());

        Product saved = productRepository.save(product);
        log.info("Created product with ID: {}", saved.getId());

        return productMapper.toDto(saved);
    }

    @Transactional(readOnly = true)
    public ProductDto getProduct(UUID id) {
        Product product = productRepository
                .findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product", id));
        return productMapper.toDto(product);
    }

    @Transactional(readOnly = true)
    public Page<ProductDto> getAllProducts(Pageable pageable) {
        return productRepository.findAll(pageable).map(productMapper::toDto);
    }

    @Transactional
    public ProductDto restockProduct(UUID id, int quantity) {
        log.info("Restocking product {} with quantity: {}", id, quantity);

        Product product = productRepository
                .findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product", id));

        product.restock(quantity);
        Product saved = productRepository.save(product);

        log.info("Product {} restocked. New stock: {}", id, saved.getStockQuantity());
        return productMapper.toDto(saved);
    }
}



