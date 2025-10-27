package com.coffeeshop.application.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/**
 * Simulated service to demonstrate I/O-bound operations with Virtual Threads
 */
@Service
public class ShippingService {
    private static final Logger log = LoggerFactory.getLogger(ShippingService.class);

    public double estimateShipping(String customerId) {
        log.debug("Estimating shipping cost for customer: {}", customerId);

        // Simulate I/O delay (e.g., calling external shipping API)
        try {
            Thread.sleep(150);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Shipping estimation interrupted", e);
        }

        // Simple shipping cost logic
        return 5.99;
    }
}



