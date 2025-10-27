package com.coffeeshop.api.controller;

import com.coffeeshop.application.dto.CreateProductRequest;
import com.coffeeshop.application.dto.ProductDto;
import com.coffeeshop.application.dto.RestockRequest;
import com.coffeeshop.application.service.ProductService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/products")
@Tag(name = "Products", description = "Product management endpoints")
public class ProductController {
    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    @SecurityRequirement(name = "bearer-jwt")
    @Operation(summary = "Create product", description = "Create a new product (Admin only)")
    public ResponseEntity<ProductDto> createProduct(@Valid @RequestBody CreateProductRequest request) {
        ProductDto created = productService.createProduct(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get product by ID", description = "Retrieve a product by its ID")
    public ResponseEntity<ProductDto> getProduct(@PathVariable UUID id) {
        ProductDto product = productService.getProduct(id);
        return ResponseEntity.ok(product);
    }

    @GetMapping
    @Operation(
            summary = "Get all products",
            description = "Retrieve all products with pagination and sorting")
    public ResponseEntity<Page<ProductDto>> getAllProducts(
            @PageableDefault(size = 20, sort = "name", direction = Sort.Direction.ASC)
                    Pageable pageable) {
        Page<ProductDto> products = productService.getAllProducts(pageable);
        return ResponseEntity.ok(products);
    }

    @PatchMapping("/{id}/restock")
    @PreAuthorize("hasRole('ADMIN')")
    @SecurityRequirement(name = "bearer-jwt")
    @Operation(summary = "Restock product", description = "Add inventory to a product (Admin only)")
    public ResponseEntity<ProductDto> restockProduct(
            @PathVariable UUID id, @Valid @RequestBody RestockRequest request) {
        ProductDto product = productService.restockProduct(id, request.quantity());
        return ResponseEntity.ok(product);
    }
}



