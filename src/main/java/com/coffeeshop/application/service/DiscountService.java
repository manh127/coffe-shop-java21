package com.coffeeshop.application.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/**
 * Simulated service to demonstrate I/O-bound operations with Virtual Threads
 */
@Service
public class DiscountService {
    private static final Logger log = LoggerFactory.getLogger(DiscountService.class);

    public double calculateDiscount(String customerId) {
        log.debug("Calculating discount for customer: {}", customerId);

        // Simulate I/O delay (e.g., calling external pricing service)
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Discount calculation interrupted", e);
        }

        // Simple discount logic
        return customerId.hashCode() % 2 == 0 ? 10.0 : 5.0;
    }
}



