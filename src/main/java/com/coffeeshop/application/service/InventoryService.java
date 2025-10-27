package com.coffeeshop.application.service;

import com.coffeeshop.domain.product.Product;
import com.coffeeshop.domain.product.ProductRepository;
import com.coffeeshop.domain.product.StockLowEvent;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class InventoryService {
    private static final Logger log = LoggerFactory.getLogger(InventoryService.class);

    private final ProductRepository productRepository;
    private final ApplicationEventPublisher eventPublisher;

    @Value("${app.inventory.low-stock-threshold}")
    private int lowStockThreshold;

    @Value("${app.inventory.restock-amount}")
    private int restockAmount;

    public InventoryService(
            ProductRepository productRepository, ApplicationEventPublisher eventPublisher) {
        this.productRepository = productRepository;
        this.eventPublisher = eventPublisher;
    }

    @Scheduled(cron = "${app.scheduler.restock-check-cron}")
    @Transactional
    public void checkLowStockProducts() {
        log.info("Running scheduled low stock check with threshold: {}", lowStockThreshold);

        List<Product> lowStockProducts = productRepository.findLowStockProducts(lowStockThreshold);

        if (lowStockProducts.isEmpty()) {
            log.info("No low stock products found");
            return;
        }

        log.warn("Found {} products with low stock", lowStockProducts.size());

        for (Product product : lowStockProducts) {
            StockLowEvent event = StockLowEvent.of(product, lowStockThreshold);
            eventPublisher.publishEvent(event);
            log.warn(
                    "Published StockLowEvent for product: {} (current: {}, threshold: {})",
                    product.getName(),
                    product.getStockQuantity(),
                    lowStockThreshold);
        }
    }

    @Transactional
    public void autoRestock(Product product) {
        log.info(
                "Auto-restocking product: {} with {} units",
                product.getName(),
                restockAmount);
        product.restock(restockAmount);
        productRepository.save(product);
    }
}



