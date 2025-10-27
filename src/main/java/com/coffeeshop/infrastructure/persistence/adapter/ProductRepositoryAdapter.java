package com.coffeeshop.infrastructure.persistence.adapter;

import com.coffeeshop.domain.product.Product;
import com.coffeeshop.domain.product.ProductRepository;
import com.coffeeshop.infrastructure.persistence.entity.ProductEntity;
import com.coffeeshop.infrastructure.persistence.jpa.JpaProductRepository;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

@Repository
public class ProductRepositoryAdapter implements ProductRepository {
    private final JpaProductRepository jpaRepository;

    public ProductRepositoryAdapter(JpaProductRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public Product save(Product product) {
        ProductEntity entity = ProductEntity.fromDomain(product);
        ProductEntity saved = jpaRepository.save(entity);
        return saved.toDomain();
    }

    @Override
    public Optional<Product> findById(UUID id) {
        return jpaRepository.findById(id).map(ProductEntity::toDomain);
    }

    @Override
    public Optional<Product> findBySku(String sku) {
        return jpaRepository.findBySku(sku).map(ProductEntity::toDomain);
    }

    @Override
    public List<Product> findAllById(List<UUID> ids) {
        return jpaRepository.findAllById(ids).stream()
                .map(ProductEntity::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public Page<Product> findAll(Pageable pageable) {
        return jpaRepository.findAll(pageable).map(ProductEntity::toDomain);
    }

    @Override
    public List<Product> findLowStockProducts(int threshold) {
        return jpaRepository.findLowStockProducts(threshold).stream()
                .map(ProductEntity::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public boolean existsBySku(String sku) {
        return jpaRepository.existsBySku(sku);
    }

    @Override
    public void delete(Product product) {
        jpaRepository.deleteById(product.getId());
    }
}



