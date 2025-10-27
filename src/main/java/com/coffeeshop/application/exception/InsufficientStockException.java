package com.coffeeshop.application.exception;

public class InsufficientStockException extends BusinessException {
    public InsufficientStockException(String productName, int available, int requested) {
        super(
                String.format(
                        "Insufficient stock for product '%s'. Available: %d, Requested: %d",
                        productName, available, requested),
                "INSUFFICIENT_STOCK");
    }
}



