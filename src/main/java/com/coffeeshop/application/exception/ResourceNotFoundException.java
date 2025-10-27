package com.coffeeshop.application.exception;

import java.util.UUID;

public class ResourceNotFoundException extends BusinessException {
    public ResourceNotFoundException(String resourceName, UUID id) {
        super(resourceName + " not found with id: " + id, "RESOURCE_NOT_FOUND");
    }

    public ResourceNotFoundException(String message) {
        super(message, "RESOURCE_NOT_FOUND");
    }
}



