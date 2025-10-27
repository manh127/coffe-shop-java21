package com.coffeeshop.application.event;

import com.coffeeshop.domain.product.StockLowEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
public class StockLowEventHandler {
    private static final Logger log = LoggerFactory.getLogger(StockLowEventHandler.class);

    @EventListener
    public void handleStockLowEvent(StockLowEvent event) {
        log.warn(
                "ALERT: Low stock detected for product: {} (ID: {}). Current stock: {}, Threshold: {}",
                event.productName(),
                event.productId(),
                event.currentStock(),
                event.threshold());

        // In a real application, this could:
        // - Send email notification
        // - Create purchase order
        // - Update inventory management system
        // - Trigger auto-restock workflow
    }
}



