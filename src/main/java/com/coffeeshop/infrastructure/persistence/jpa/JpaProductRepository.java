package com.coffeeshop.infrastructure.persistence.jpa;

import com.coffeeshop.infrastructure.persistence.entity.ProductEntity;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface JpaProductRepository extends JpaRepository<ProductEntity, UUID> {
    Optional<ProductEntity> findBySku(String sku);

    boolean existsBySku(String sku);

    Page<ProductEntity> findAll(Pageable pageable);

    @Query("SELECT p FROM ProductEntity p WHERE p.stockQuantity < :threshold")
    List<ProductEntity> findLowStockProducts(@Param("threshold") int threshold);
}



