package com.coffeeshop.api.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.coffeeshop.application.dto.CreateProductRequest;
import com.coffeeshop.application.dto.ProductDto;
import com.coffeeshop.application.service.ProductService;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.math.BigDecimal;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(ProductController.class)
@Import(com.coffeeshop.infrastructure.config.SecurityTestConfig.class)
class ProductControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ProductService productService;

    @Test
    @WithMockUser(roles = "ADMIN")
    void shouldCreateProduct() throws Exception {
        CreateProductRequest request =
                new CreateProductRequest("Espresso", "COFFEE-ESP-001", BigDecimal.valueOf(2.50), 100);
        ProductDto response = new ProductDto(
                UUID.randomUUID(), "Espresso", "COFFEE-ESP-001", BigDecimal.valueOf(2.50), 100, null, null);

        when(productService.createProduct(any(CreateProductRequest.class))).thenReturn(response);

        mockMvc.perform(post("/api/v1/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("Espresso"))
                .andExpect(jsonPath("$.sku").value("COFFEE-ESP-001"));
    }

    @Test
    void shouldGetProduct() throws Exception {
        UUID productId = UUID.randomUUID();
        ProductDto response = new ProductDto(
                productId, "Espresso", "COFFEE-ESP-001", BigDecimal.valueOf(2.50), 100, null, null);

        when(productService.getProduct(productId)).thenReturn(response);

        mockMvc.perform(get("/api/v1/products/" + productId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(productId.toString()))
                .andExpect(jsonPath("$.name").value("Espresso"));
    }
}



