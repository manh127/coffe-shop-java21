package com.coffeeshop.domain.product;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ProductRepository {
    Product save(Product product);

    Optional<Product> findById(UUID id);

    Optional<Product> findBySku(String sku);

    List<Product> findAllById(List<UUID> ids);

    Page<Product> findAll(Pageable pageable);

    List<Product> findLowStockProducts(int threshold);

    boolean existsBySku(String sku);

    void delete(Product product);
}



